import CoffeeSelector from "./CoffeeSelector";
import ProgressRing from "./ProgressRing";
import RecentOrders from "./RecentOrders";

import useMachineProgress from "../hooks/useMachineProgress";

import styles from "./Coffee.module.css";

function Coffee() {
    const {progress, type} = useMachineProgress();

    return (
        <main className={styles.root}>
            <ProgressRing
                progress={progress}
                type={type}
            />
            <CoffeeSelector />
            <RecentOrders />
        </main>
    );
}

export default Coffee;
