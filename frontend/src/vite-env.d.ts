/// <reference types="vite/client" />
// noinspection JSUnusedGlobalSymbols

// extend Vite's built-in environment
// variable types with project-specific variables
interface ImportMetaEnv {
    readonly VITE_BACKEND_URL: string;
}

// declare SVG imports as React components
// when using the ?react import suffix
declare module "*.svg?react" {
    import type { FC, SVGProps } from "react";

    const Component: FC<SVGProps<SVGSVGElement>>;
    export default Component;
}
