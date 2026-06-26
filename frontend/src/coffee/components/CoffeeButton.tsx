import type { CoffeeType } from "../types";

import AmericanoIcon from "../assets/americano.svg?react";
import CappuccinoIcon from "../assets/cappuccino.svg?react";
import EspressoIcon from "../assets/espresso.svg?react";

import styles from "./CoffeeButton.module.css";

type CoffeeButtonProps = {
    type: CoffeeType;
    selected: boolean;
    onClick: () => void;
};

const icons = {
    ESPRESSO: EspressoIcon,
    AMERICANO: AmericanoIcon,
    CAPPUCCINO: CappuccinoIcon,
};

function CoffeeButton({
    type,
    selected,
    onClick,
}: CoffeeButtonProps) {
    const Icon = icons[type];
    return (
        <button
            className={`${styles.button} ${selected ? styles.selected : ""}`}
            onClick={onClick}
            type="button"
        >
            <Icon className={styles.icon} />
            <span className={styles.label}>
                {type}
            </span>
            <div className={styles.indicator} />
        </button>
    );
}

export default CoffeeButton;
