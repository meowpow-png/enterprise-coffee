import type {
    CoffeeOrderRequest,
    CoffeeOrderResponse,
    CoffeeOrdersResponse,
    MachineProgressResponse,
    MachineStatusResponse
} from "./types";

const BACKEND_URL = import.meta.env.VITE_BACKEND_URL;

type HttpMethod = "GET" | "POST";

export async function getOrders(
    limit?: number,
): Promise<CoffeeOrdersResponse> {
    const path = limit === undefined
        ? "/orders"
        : `/orders?limit=${limit}`;

    return request("GET", path);
}

export async function getStatus(): Promise<MachineStatusResponse> {
    return request("GET", "/status");
}

export async function getProgress(): Promise<MachineProgressResponse> {
    return request("GET", "/progress");
}

export async function submitOrder(
    orderRequest: CoffeeOrderRequest,
): Promise<CoffeeOrderResponse> {
    return request("POST", "/order", orderRequest);
}

async function request<T>(
    method: HttpMethod,
    path: string,
    body?: unknown,
): Promise<T> {
    const url = `${BACKEND_URL}/${normalizePath(path)}`;
    const options: RequestInit = {
        method,
        headers: {
            "Content-Type": "application/json",
        },
        body: body === undefined
            ? undefined
            : JSON.stringify(body),
    };
    const response = await fetch(url, options);

    if (!response.ok) {
        const message = `Request failed: ${response.status} ${response.statusText}`;
        throw new Error(message);
    }
    const result = await response.json();
    return result as T;
}

function normalizePath(path: string): string {
    return path.replace(/^\/+/, "");
}
