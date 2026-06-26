import type { CoffeeType } from "../types";

import styles from "./ProgressRing.module.css";

type ProgressRingProps = {
    progress: number;
    type: CoffeeType | null;
};

const RADIUS = 126;
const CIRCUMFERENCE = 2 * Math.PI * RADIUS;

function ProgressRing({ progress, type }: ProgressRingProps) {
    const offset = CIRCUMFERENCE - (progress / 100) * CIRCUMFERENCE;

    return (
        <section className={styles.root}>
            <div className={styles.header}>
                {type !== null && (
                    <p className={styles.title}>
                        {type}
                    </p>
                )}
                <p className={styles.subtitle}>
                    {type === null ? "Ready to brew" : "Brewing..."}
                </p>
            </div>
            <div className={styles.ring}>
                <svg
                    className={styles.svg}
                    viewBox="0 0 300 300"
                >
                    <circle
                        className={styles.track}
                        cx="150"
                        cy="150"
                        r={RADIUS}
                        fill="none"
                        strokeWidth="10"
                    />
                    <circle
                        className={styles.progress}
                        cx="150"
                        cy="150"
                        r={RADIUS}
                        fill="none"
                        strokeWidth="10"
                        strokeDasharray={CIRCUMFERENCE}
                        strokeDashoffset={offset}
                    />
                </svg>
                <div className={styles.content}>
                    <p className={styles.percentage}>
                        {progress}
                        <span className={styles.symbol}>%</span>
                    </p>
                </div>
            </div>
        </section>
    );
}

export default ProgressRing;