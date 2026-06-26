export type CoffeeType =
    | "ESPRESSO"
    | "AMERICANO"
    | "CAPPUCCINO";

export type MachineStatus =
    | "READY"
    | "BREWING";

export type CoffeeState = {
    status: MachineStatus;
    type: CoffeeType | null;
    progress: number;
};
