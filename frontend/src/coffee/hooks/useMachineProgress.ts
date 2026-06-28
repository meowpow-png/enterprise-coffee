import {useState, useEffect} from 'react';
import coffeeService from "../coffee";
import env from "@/env";

import type {CoffeeType, MachineProgress} from "..";

const REFRESH_INTERVAL_MILLIS = env.getNumber(
    "VITE_REFRESH_INTERVAL_MILLIS",
    500
);

/**
 * Tracks the current coffee brewing progress.
 *
 * <b>Implementation Note:</b>
 * Fetches the current machine progress when the hook
 * is initialized and refreshes it periodically
 * until the component is unmounted.
 */
export function useMachineProgress(): MachineProgress {
    const [progress, setProgress] = useState(0);
    const [type, setType] = useState<CoffeeType | null>(null);

    useEffect(() => {
        async function loadProgress(): Promise<void> {
            const machineProgress = await coffeeService.getProgress();

            setProgress(machineProgress.progress);
            setType(machineProgress.type);
        }
        void loadProgress();
        const interval = setInterval(() => {
            void loadProgress();
        }, REFRESH_INTERVAL_MILLIS);

        return () => {
            clearInterval(interval);
        };
    }, []);

    return {
        type,
        progress,
    };
}

export default useMachineProgress;
