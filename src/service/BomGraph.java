package service;

import entity.BOM;
import entity.ItemMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/** 校验后的 BOM 是展开顺序和低层码的唯一依据。 */
final class BomGraph {
    final Map<String, ItemMaster> items = new HashMap<String, ItemMaster>();
    final Map<String, List<BOM>> incoming = new HashMap<String, List<BOM>>();
    final List<ItemMaster> order = new ArrayList<ItemMaster>();

    BomGraph(List<ItemMaster> itemList, List<BOM> bomList) {
        if (itemList == null || bomList == null) {
            throw new IllegalArgumentException("Item master and BOM lists are required");
        }
        Map<String, List<BOM>> outgoing = new HashMap<String, List<BOM>>();
        for (ItemMaster item : itemList) {
            if (item == null) throw new IllegalArgumentException("Null item master row");
            String part = requirePart(item.getP_No());
            if (items.put(part, item) != null) throw new IllegalArgumentException("Duplicate item: " + part);
            incoming.put(part, new ArrayList<BOM>());
            outgoing.put(part, new ArrayList<BOM>());
        }
        Set<String> edges = new HashSet<String>();
        for (BOM edge : bomList) {
            if (edge == null) throw new IllegalArgumentException("Null BOM row");
            String parent = requirePart(edge.getParent());
            String child = requirePart(edge.getComp());
            if (!items.containsKey(parent) || !items.containsKey(child)) {
                throw new IllegalArgumentException("BOM references unknown item: " + parent + " -> " + child);
            }
            if (edge.getQ_P() <= 0) throw new IllegalArgumentException("BOM quantity must be positive: " + parent + " -> " + child);
            if (!edges.add(parent + "\u0000" + child)) throw new IllegalArgumentException("Duplicate BOM edge: " + parent + " -> " + child);
            outgoing.get(parent).add(edge);
            incoming.get(child).add(edge);
        }
        sortAndValidateCodes(outgoing);
    }

    private void sortAndValidateCodes(Map<String, List<BOM>> outgoing) {
        Map<String, Integer> remaining = new HashMap<String, Integer>();
        Map<String, Integer> levels = new HashMap<String, Integer>();
        PriorityQueue<String> ready = new PriorityQueue<String>();
        for (String part : items.keySet()) {
            remaining.put(part, incoming.get(part).size());
            levels.put(part, 0);
            if (incoming.get(part).isEmpty()) ready.add(part);
        }
        while (!ready.isEmpty()) {
            String part = ready.remove();
            ItemMaster item = items.get(part);
            order.add(item);
            for (BOM edge : outgoing.get(part)) {
                String child = edge.getComp();
                levels.put(child, Math.max(levels.get(child), levels.get(part) + 1));
                int count = remaining.get(child) - 1;
                remaining.put(child, count);
                if (count == 0) ready.add(child);
            }
        }
        if (order.size() != items.size()) throw new IllegalArgumentException("BOM contains a cycle");
        for (ItemMaster item : order) {
            int expected = levels.get(item.getP_No());
            if (item.getLLC() != expected) {
                throw new IllegalArgumentException("LLC mismatch for " + item.getP_No() + ": expected " + expected + ", actual " + item.getLLC());
            }
        }
    }

    static String requirePart(String part) {
        if (part == null || part.trim().isEmpty() || !part.equals(part.trim()) || part.indexOf('\u0000') >= 0) {
            throw new IllegalArgumentException("Part number is blank or invalid");
        }
        return part;
    }
}
