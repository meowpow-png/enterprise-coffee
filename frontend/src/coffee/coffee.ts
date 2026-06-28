import {
    getOrders as getOrdersRequest,
    getProgress as getProgressRequest,
    getStatus as getStatusRequest,
    submitOrder as submitOrderRequest,
} from "./api";
import type {
    CoffeeOrder,
    CoffeeOrderRequest,
    CoffeeType,
    MachineProgress,
    MachineStatus,
} from "./types";

/**
 * Application service providing
 * coffee-related operations.
 */
export interface CoffeeService {
    submitOrder(type: CoffeeType): Promise<void>;
    getOrders(limit?: number): Promise<readonly CoffeeOrder[]>;
    getStatus(): Promise<MachineStatus>;
    getProgress(): Promise<MachineProgress>;
}

/**
 * Submits a coffee order.
 *
 * @param type coffee type to order
 */
async function submitOrder(type: CoffeeType): Promise<void> {
    const request: CoffeeOrderRequest = {
        type,
    };
    await submitOrderRequest(request);
}

/**
 * Returns the latest coffee orders.
 *
 * @param limit maximum number of coffee orders to return
 */
async function getOrders(
    limit?: number,
): Promise<readonly CoffeeOrder[]> {
    const response = await getOrdersRequest(limit);
    return response.orders;
}

/**
 * Returns the current machine status.
 */
async function getStatus(): Promise<MachineStatus> {
    const response = await getStatusRequest();
    return response.status;
}

/**
 * Returns the current brewing progress.
 *
 * <b>Implementation Note:</b>
 * Converts backend representation of an idle machine
 * (empty coffee type) into the frontend domain model (`null`).
 */
async function getProgress(): Promise<MachineProgress> {
    const response = await getProgressRequest();
    return {
        progress: response.progress,
        type: response.type === "" ? null : response.type,
    };
}

/**
 * Application service providing
 * coffee-related operations.
 */
const coffeeService: CoffeeService = {
    submitOrder,
    getOrders,
    getStatus,
    getProgress,
};

export default coffeeService;
