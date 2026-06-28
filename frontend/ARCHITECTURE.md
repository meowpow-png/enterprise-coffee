# Architecture

## Overview

The frontend follows a feature-oriented architecture that groups related UI, application logic, and backend communication into self-contained modules.

The architecture emphasizes explicit data flow, clear separation of responsibilities, and semantic boundaries to keep the codebase easy to understand and maintain.

## Project Structure

The frontend is organized into feature modules and shared infrastructure.

```text
src/
    coffee/
    shared/
    App.tsx
    main.tsx
```

| Module     | Responsibility                                                            |
|------------|---------------------------------------------------------------------------|
| `coffee/`  | Coffee-related UI, application logic, and backend communication           |
| `shared/`  | Reusable components, hooks, and utilities shared across features          |
| `App.tsx`  | Root application component responsible for composing the application      |
| `main.tsx` | Application entry point that initializes and mounts the React application |

## Feature Modules

Feature modules group together the UI, application logic, backend communication, and types for a single business capability.

A typical feature contains:

* `components/` — feature-specific UI components
* `hooks/` — feature-specific application logic
* `api.ts` — backend communication
* `types.ts` — feature-specific types
* `index.ts` — barrel that re-exports the feature's public API

**Rules**

* Features own their UI, application logic, and backend communication
* Features should be self-contained and must not depend on other features
* Consumers should import from the feature barrel rather than its internal modules

## Imports

Feature barrels (`index.ts`) define the public API of a feature. They provide a single entry point for consumers outside the feature and decouple them from its internal file structure.

Files within a feature should import directly from feature modules rather than through the feature barrel. This preserves clear layering and avoids feature implementations depending on their own public API.

Imports should be grouped by their role within the module:

1. Runtime dependencies 
2. Type-only imports
3. Static assets such as styles and images

**Example**

```ts
import OrderRow from "./OrderRow";
import coffeeService from "../coffee";

import type { CoffeeOrder } from "../types";

import styles from "./RecentOrders.module.css";
```

## Shared Modules

Shared modules contain reusable code that is used across multiple features.

Typical examples include:

- reusable UI components
- reusable hooks
- utility functions

**Rules**

- Shared modules must not depend on feature modules.

## Backend Communication

Backend communication is encapsulated by feature API modules. This keeps HTTP concerns separated from UI and application logic.

**Rules**

- Components must never communicate with the backend directly
- Hooks may use feature API modules but must not implement HTTP communication
- HTTP concerns (URLs, headers, request handling, and response parsing) belong in feature API modules

```text
Component
    ↓
Hook
    ↓
API
    ↓
Backend
```

## Application Bootstrap

`App.tsx`

Root application component responsible for composing the application.

`main.tsx`

Application entry point responsible for initializing and mounting the React application.

## Dependency Rules

```text
feature
    ↓
shared

App
    ↓
feature

main
    ↓
App
```

**Rules**

- Features must not depend on other features
- Shared modules must not import from feature modules
