-- 在完成 V1 的示例库上执行一次。全部使用 InnoDB；不修改已有业务输入和结果。
CREATE TABLE t_mrp_recalculation_lock (
    id INT NOT NULL PRIMARY KEY
) ENGINE=InnoDB;
INSERT INTO t_mrp_recalculation_lock (id) VALUES (1);

CREATE TABLE t_mrp_past_due_release (
    mrp_id INT NOT NULL,
    period INT NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (mrp_id, period),
    FOREIGN KEY (mrp_id) REFERENCES t_mrp(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE t_mrp_past_due_demand (
    mrp_id INT NOT NULL,
    period INT NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (mrp_id, period),
    FOREIGN KEY (mrp_id) REFERENCES t_mrp(id) ON DELETE CASCADE
) ENGINE=InnoDB;
