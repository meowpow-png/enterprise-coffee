import CoffeeButton from "./CoffeeButton";

import styles from "./CoffeeSelector.module.css";

function CoffeeSelector() {
    return (
        <section className={styles.root}>
            <h2 className={styles.title}>
                Choose your coffee
            </h2>
            <div className={styles.buttons}>
                <CoffeeButton
                    type="ESPRESSO"
                    selected={true}
                    onClick={() => {}}
                />

                <CoffeeButton
                    type="AMERICANO"
                    selected={false}
                    onClick={() => {}}
                />

                <CoffeeButton
                    type="CAPPUCCINO"
                    selected={false}
                    onClick={() => {}}
                />
            </div>
        </section>
    );
}

export default CoffeeSelector;
