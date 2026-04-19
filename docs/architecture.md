# XLibrary Architecture

The XLibrary project is divided into modules to ensure maximum reusability of components.

## Application Module: `core`
The **Core** module does not contain any Bukkit/Paper classes. It contains exclusively pure Java components:
- **Collections** (`ExpiringSet`, etc.)
- Basic interfaces (`Identifiable`)

## Environment Module: `paper`
The **Paper** module depends on `core` and implements the functionality for the Paper server API:
- **Command API**
- **GUI Utilities**
- **Schematic API**

This separation ensures that in the future, the `core` module can be reused on proxies like BungeeCord / Velocity without conflicting with Bukkit.
