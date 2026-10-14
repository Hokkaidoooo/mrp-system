-- Normalize fixed period columns into period line tables.
-- This migration keeps the legacy tables intact for the current Struts UI.

ALTER TABLE t_itemmaster
    MODIFY P_No varchar(255) NOT NULL,
    MODIFY LT int(11) NOT NULL DEFAULT 0,
    MODIFY ST int(11) NOT NULL DEFAULT 0,
    MODIFY SS int(11) NOT NULL DEFAULT 0,
    MODIFY LLC int(11) NOT NULL DEFAULT 0,
    MODIFY LSR varchar(255) NOT NULL DEFAULT 'LFL',
    MODIFY LS int(11) NOT NULL DEFAULT 0;

ALTER TABLE t_inventory
    MODIFY P_No varchar(255) NOT NULL,
    MODIFY OH varchar(255) NOT NULL DEFAULT '0',
    MODIFY AL varchar(255) NOT NULL DEFAULT '0';

ALTER TABLE t_bom
    MODIFY parent varchar(255) NOT NULL,
    MODIFY comp varchar(255) NOT NULL,
    MODIFY Q_P int(11) NOT NULL DEFAULT 1;

ALTER TABLE t_tpop
    MODIFY P_No varchar(255) NOT NULL;

ALTER TABLE t_mrp
    MODIFY P_No varchar(255) NOT NULL;

CREATE UNIQUE INDEX uk_itemmaster_part_no ON t_itemmaster (P_No);
CREATE UNIQUE INDEX uk_inventory_part_no ON t_inventory (P_No);
CREATE UNIQUE INDEX uk_tpop_part_no ON t_tpop (P_No);
CREATE INDEX idx_bom_parent ON t_bom (parent);
CREATE INDEX idx_bom_comp ON t_bom (comp);

ALTER TABLE t_bom
    ADD CONSTRAINT fk_bom_parent_item FOREIGN KEY (parent) REFERENCES t_itemmaster (P_No),
    ADD CONSTRAINT fk_bom_comp_item FOREIGN KEY (comp) REFERENCES t_itemmaster (P_No);

CREATE TABLE IF NOT EXISTS t_inventory_receipt_line (
    id int(11) NOT NULL AUTO_INCREMENT,
    P_No varchar(255) NOT NULL,
    period_no int(11) NOT NULL,
    scheduled_receipt int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventory_receipt_line_part_period (P_No, period_no),
    CONSTRAINT fk_inventory_receipt_line_part FOREIGN KEY (P_No) REFERENCES t_itemmaster (P_No)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_tpop_line (
    id int(11) NOT NULL AUTO_INCREMENT,
    P_No varchar(255) NOT NULL,
    period_no int(11) NOT NULL,
    planned_order_release int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tpop_line_part_period (P_No, period_no),
    CONSTRAINT fk_tpop_line_part FOREIGN KEY (P_No) REFERENCES t_itemmaster (P_No)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_mrp_result_line (
    id int(11) NOT NULL AUTO_INCREMENT,
    P_No varchar(255) NOT NULL,
    period_no int(11) NOT NULL,
    gross_requirement int(11) NOT NULL DEFAULT 0,
    scheduled_receipt int(11) NOT NULL DEFAULT 0,
    projected_on_hand int(11) NOT NULL DEFAULT 0,
    projected_available_balance int(11) NOT NULL DEFAULT 0,
    net_requirement int(11) NOT NULL DEFAULT 0,
    planned_order_receipt int(11) NOT NULL DEFAULT 0,
    planned_order_release int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_mrp_result_line_part_period (P_No, period_no),
    CONSTRAINT fk_mrp_result_line_part FOREIGN KEY (P_No) REFERENCES t_itemmaster (P_No)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO t_inventory_receipt_line (P_No, period_no, scheduled_receipt)
SELECT P_No, 0, COALESCE(OO0, 0) FROM t_inventory UNION ALL
SELECT P_No, 1, COALESCE(OO1, 0) FROM t_inventory UNION ALL
SELECT P_No, 2, COALESCE(OO2, 0) FROM t_inventory UNION ALL
SELECT P_No, 3, COALESCE(OO3, 0) FROM t_inventory UNION ALL
SELECT P_No, 4, COALESCE(OO4, 0) FROM t_inventory UNION ALL
SELECT P_No, 5, COALESCE(OO5, 0) FROM t_inventory UNION ALL
SELECT P_No, 6, COALESCE(OO6, 0) FROM t_inventory UNION ALL
SELECT P_No, 7, COALESCE(OO7, 0) FROM t_inventory UNION ALL
SELECT P_No, 8, COALESCE(OO8, 0) FROM t_inventory UNION ALL
SELECT P_No, 9, COALESCE(OO9, 0) FROM t_inventory UNION ALL
SELECT P_No, 10, COALESCE(OO10, 0) FROM t_inventory UNION ALL
SELECT P_No, 11, COALESCE(OO11, 0) FROM t_inventory UNION ALL
SELECT P_No, 12, COALESCE(OO12, 0) FROM t_inventory;

INSERT INTO t_tpop_line (P_No, period_no, planned_order_release)
SELECT P_No, 0, COALESCE(POR0, 0) FROM t_tpop UNION ALL
SELECT P_No, 1, COALESCE(POR1, 0) FROM t_tpop UNION ALL
SELECT P_No, 2, COALESCE(POR2, 0) FROM t_tpop UNION ALL
SELECT P_No, 3, COALESCE(POR3, 0) FROM t_tpop UNION ALL
SELECT P_No, 4, COALESCE(POR4, 0) FROM t_tpop UNION ALL
SELECT P_No, 5, COALESCE(POR5, 0) FROM t_tpop UNION ALL
SELECT P_No, 6, COALESCE(POR6, 0) FROM t_tpop UNION ALL
SELECT P_No, 7, COALESCE(POR7, 0) FROM t_tpop UNION ALL
SELECT P_No, 8, COALESCE(POR8, 0) FROM t_tpop UNION ALL
SELECT P_No, 9, COALESCE(POR9, 0) FROM t_tpop UNION ALL
SELECT P_No, 10, COALESCE(POR10, 0) FROM t_tpop UNION ALL
SELECT P_No, 11, COALESCE(POR11, 0) FROM t_tpop UNION ALL
SELECT P_No, 12, COALESCE(POR12, 0) FROM t_tpop;

INSERT INTO t_mrp_result_line (
    P_No, period_no, gross_requirement, scheduled_receipt, projected_on_hand,
    projected_available_balance, net_requirement, planned_order_receipt, planned_order_release
)
SELECT P_No, 0, COALESCE(GR0, 0), COALESCE(SR0, 0), COALESCE(POH0, 0), COALESCE(PAB0, 0), COALESCE(NR0, 0), COALESCE(PORcpt0, 0), COALESCE(POR0, 0) FROM t_mrp UNION ALL
SELECT P_No, 1, COALESCE(GR1, 0), COALESCE(SR1, 0), COALESCE(POH1, 0), COALESCE(PAB1, 0), COALESCE(NR1, 0), COALESCE(PORcpt1, 0), COALESCE(POR1, 0) FROM t_mrp UNION ALL
SELECT P_No, 2, COALESCE(GR2, 0), COALESCE(SR2, 0), COALESCE(POH2, 0), COALESCE(PAB2, 0), COALESCE(NR2, 0), COALESCE(PORcpt2, 0), COALESCE(POR2, 0) FROM t_mrp UNION ALL
SELECT P_No, 3, COALESCE(GR3, 0), COALESCE(SR3, 0), COALESCE(POH3, 0), COALESCE(PAB3, 0), COALESCE(NR3, 0), COALESCE(PORcpt3, 0), COALESCE(POR3, 0) FROM t_mrp UNION ALL
SELECT P_No, 4, COALESCE(GR4, 0), COALESCE(SR4, 0), COALESCE(POH4, 0), COALESCE(PAB4, 0), COALESCE(NR4, 0), COALESCE(PORcpt4, 0), COALESCE(POR4, 0) FROM t_mrp UNION ALL
SELECT P_No, 5, COALESCE(GR5, 0), COALESCE(SR5, 0), COALESCE(POH5, 0), COALESCE(PAB5, 0), COALESCE(NR5, 0), COALESCE(PORcpt5, 0), COALESCE(POR5, 0) FROM t_mrp UNION ALL
SELECT P_No, 6, COALESCE(GR6, 0), COALESCE(SR6, 0), COALESCE(POH6, 0), COALESCE(PAB6, 0), COALESCE(NR6, 0), COALESCE(PORcpt6, 0), COALESCE(POR6, 0) FROM t_mrp UNION ALL
SELECT P_No, 7, COALESCE(GR7, 0), COALESCE(SR7, 0), COALESCE(POH7, 0), COALESCE(PAB7, 0), COALESCE(NR7, 0), COALESCE(PORcpt7, 0), COALESCE(POR7, 0) FROM t_mrp UNION ALL
SELECT P_No, 8, COALESCE(GR8, 0), COALESCE(SR8, 0), COALESCE(POH8, 0), COALESCE(PAB8, 0), COALESCE(NR8, 0), COALESCE(PORcpt8, 0), COALESCE(POR8, 0) FROM t_mrp UNION ALL
SELECT P_No, 9, COALESCE(GR9, 0), COALESCE(SR9, 0), COALESCE(POH9, 0), COALESCE(PAB9, 0), COALESCE(NR9, 0), COALESCE(PORcpt9, 0), COALESCE(POR9, 0) FROM t_mrp UNION ALL
SELECT P_No, 10, COALESCE(GR10, 0), COALESCE(SR10, 0), COALESCE(POH10, 0), COALESCE(PAB10, 0), COALESCE(NR10, 0), COALESCE(PORcpt10, 0), COALESCE(POR10, 0) FROM t_mrp UNION ALL
SELECT P_No, 11, COALESCE(GR11, 0), COALESCE(SR11, 0), COALESCE(POH11, 0), COALESCE(PAB11, 0), COALESCE(NR11, 0), COALESCE(PORcpt11, 0), COALESCE(POR11, 0) FROM t_mrp UNION ALL
SELECT P_No, 12, COALESCE(GR12, 0), COALESCE(SR12, 0), COALESCE(POH12, 0), COALESCE(PAB12, 0), COALESCE(NR12, 0), COALESCE(PORcpt12, 0), COALESCE(POR12, 0) FROM t_mrp;
