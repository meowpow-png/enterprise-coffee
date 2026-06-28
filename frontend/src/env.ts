export interface Environment {

    /**
     * Returns the value of a string environment variable.
     *
     * @param key environment variable name
     * @param defaultValue value returned if the env variable is not defined or empty
     */
    getString(key: string, defaultValue: string): string;

    /**
     * Returns the value of a numeric environment variable.
     *
     * @param key environment variable name
     * @param defaultValue value returned if the env variable is not defined or empty
     */
    getNumber(key: string, defaultValue: number): number;
}

function getString(
    key: string,
    defaultValue: string,
): string {
    const value = import.meta.env[key];
    return value === undefined || value === ""
        ? defaultValue
        : value;
}

function getNumber(
    key: string,
    defaultValue: number,
): number {
    const value = import.meta.env[key];

    return value === undefined || value === ""
        ? defaultValue
        : Number(value);
}

/**
 * Provides access to application environment variables.
 */
const env: Environment = {
    getString,
    getNumber,
};

export default env;
