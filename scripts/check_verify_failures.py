"""在本轮独立副本中制造真实失败，核对验证入口的进程退出码。"""
import argparse
import io
import os
from pathlib import Path
import shutil
import subprocess
import tempfile
import zipfile


def populate(root, destination, revision):
    if revision:
        archive = subprocess.check_output(["git", "archive", "--format=zip", revision], cwd=root)
        with zipfile.ZipFile(io.BytesIO(archive)) as source:
            for entry in source.infolist():
                target = (destination / entry.filename).resolve()
                if destination.resolve() not in target.parents:
                    raise ValueError("Archive entry leaves the owned test directory")
            source.extractall(destination)
        return
    paths = subprocess.check_output(["git", "ls-files", "-c", "-o", "--exclude-standard", "-z"], cwd=root).decode("utf-8").split("\0")
    for relative in set(paths) - {""}:
        source = root / relative
        if source.is_file():
            target = destination / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(source, target)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--revision", help="指定旧提交仅记录其退出码；默认检查当前修改")
    args = parser.parse_args()
    root = Path(__file__).resolve().parents[1]
    owned = root / ".verification"
    owned.mkdir(exist_ok=True)
    scenarios = ("compile", "calculation", "package", "missing-maven")
    failures = []
    for scenario in scenarios:
        work = Path(tempfile.mkdtemp(prefix="verify-" + scenario + "-", dir=owned))
        try:
            populate(root, work, args.revision)
            if scenario == "compile":
                source = work / "src" / "action" / "MRPAction.java"
                source.write_text("THIS_MUST_NOT_COMPILE\n" + source.read_text(encoding="utf-8"), encoding="utf-8")
            if scenario == "calculation":
                source = work / "test" / "util" / "MrpPeriodCalculatorTest.java"
                original = source.read_text(encoding="utf-8")
                changed = original.replace("assertEquals(7, result.scheduledReceipts[12]", "assertEquals(8, result.scheduledReceipts[12]", 1)
                if changed == original:
                    raise ValueError("Cannot locate the independent hand-calculated test assertion")
                source.write_text(changed, encoding="utf-8")
            if scenario == "package":
                source = work / "pom.xml"
                original = source.read_text(encoding="utf-8")
                changed = original.replace("<warSourceDirectory>web</warSourceDirectory>", "<warSourceDirectory>pom.xml</warSourceDirectory>")
                if changed == original:
                    raise ValueError("Cannot locate WAR source configuration")
                source.write_text(changed, encoding="utf-8")
            env = os.environ.copy()
            if scenario == "missing-maven":
                key = next(key for key in env if key.lower() == "path")
                env[key] = os.pathsep.join(part for part in env[key].split(os.pathsep) if not (Path(part) / "mvn.cmd").is_file())
            result = subprocess.run(["powershell", "-NoProfile", "-File", str(work / "scripts" / "verify.ps1")], cwd=work,
                                    env=env, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, timeout=180)
            log = result.stdout.decode("utf-8", errors="replace")
            evidence = [line.strip() for line in log.splitlines() if "BUILD FAILURE" in line or "AssertionError" in line or "COMPILATION ERROR" in line or "failed with exit code" in line]
            print(f"{scenario}: exit={result.returncode}; evidence={' | '.join(evidence[-3:])}", flush=True)
            if not args.revision and result.returncode == 0:
                failures.append(scenario)
        finally:
            if work.resolve().parent != owned.resolve() or not work.name.startswith("verify-"):
                raise ValueError("Refuse to clean a directory outside this invocation's owned copies")
            shutil.rmtree(work)
    if failures:
        raise SystemExit("Failures were masked: " + ", ".join(failures))


if __name__ == "__main__":
    main()
