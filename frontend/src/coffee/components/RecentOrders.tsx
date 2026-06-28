import OrderRow from "./OrderRow";

import type { CoffeeOrder } from "../types";

import styles from "./RecentOrders.module.css";

const orders: CoffeeOrder[] = [
    {
        id: "1",
        type: "ESPRESSO",
        status: "ACCEPTED",
        createdAt: "09:24",
    },
    {
        id: "2",
        type: "AMERICANO",
        status: "FAILED",
        createdAt: "09:18",
    },
];

function RecentOrders() {
    return (
        <section className={styles.card}>
            <h2 className={styles.title}>
                Recent orders
            </h2>
            <div className={styles.orders}>
                {orders.map((order) => (
                    <OrderRow
                        key={order.id}
                        order={order}
                    />
                ))}
            </div>
        </section>
    );
}

export default RecentOrders;
