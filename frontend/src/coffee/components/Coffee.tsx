import CoffeeSelector from "./CoffeeSelector";
import ProgressRing from "./ProgressRing";

import styles from "./Coffee.module.css";

function Coffee() {
    return (
        <main className={styles.root}>
            <ProgressRing
                progress={72}
                type="ESPRESSO"
            />
            <CoffeeSelector />
        </main>
    );
}

export default Coffee;
