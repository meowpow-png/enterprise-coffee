import {
    Ban,
    Check,
    CircleAlert,
    Clock3,
    X,
} from "lucide-react";

import type { CoffeeOrder } from "../types";

import styles from "./OrderRow.module.css";

type OrderRowProps = {
    order: CoffeeOrder;
};

const statusClasses = {
    PENDING: styles.pending,
    ACCEPTED: styles.accepted,
    REJECTED: styles.rejected,
    INVALID: styles.invalid,
    FAILED: styles.failed,
};

const statusIcons = {
    PENDING: Clock3,
    ACCEPTED: Check,
    REJECTED: Ban,
    INVALID: CircleAlert,
    FAILED: X,
};

function OrderRow({ order }: OrderRowProps) {
    const Icon = statusIcons[order.status];
    return (
        <div className={styles.row}>
            <div className={`${styles.icon} ${statusClasses[order.status]}`}>
                <Icon
                    className={styles.iconSvg}
                    strokeWidth={3}
                />
            </div>
            <div className={styles.content}>
                <span className={styles.type}>
                    {order.type}
                </span>
                <span className={styles.status}>
                    {order.status}
                </span>
            </div>
            <time
                className={styles.time}
                dateTime={order.createdAt}
            >
                {order.createdAt}
            </time>
        </div>
    );
}

export default OrderRow;
