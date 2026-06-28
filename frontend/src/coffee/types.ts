export type CoffeeType =
    | "ESPRESSO"
    | "AMERICANO"
    | "CAPPUCCINO";

export type MachineStatus =
    | "READY"
    | "BREWING"
    | "OFFLINE";

export type CoffeeOrderStatus =
    | "PENDING"
    | "ACCEPTED"
    | "REJECTED"
    | "INVALID"
    | "FAILED";

export interface CoffeeOrder {
    readonly id: string;
    readonly type: CoffeeType;
    readonly status: CoffeeOrderStatus;
    readonly createdAt: string;
}

export interface MachineProgress {
    readonly type: CoffeeType | null;
    readonly progress: number;
}

/**
 * Coffee order request submitted by the client.
 */
export interface CoffeeOrderRequest {
    type: CoffeeType;
}

/**
 * Result of a coffee order request.
 */
export interface CoffeeOrderResponse {
    readonly message: string;
}

/**
 * Collection of coffee orders.
 */
export interface CoffeeOrdersResponse {
    readonly orders: readonly CoffeeOrder[];
}

/**
 * Current operational status
 * reported by the coffee machine.
 */
export interface MachineStatusResponse {
    readonly status: MachineStatus;
}

/**
 * Current coffee brewing
 * progress reported by the machine.
 */
export interface MachineProgressResponse {
    readonly type: CoffeeType | "";
    readonly progress: number;
}
