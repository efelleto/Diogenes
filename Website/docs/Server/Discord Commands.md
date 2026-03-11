---
sidebar_position: 2
---

# Discord Commands

The **Diogenes Bot** is the primary interface for managing your licensing system. It uses Slash Commands (`/`) for a modern and intuitive experience.

---

## Admin Commands
These commands are restricted to users with **Administrator** permissions on the server.

### /create-product
**Description:** Registers a new product into the database.
- **id:** The internal identifier (e.g., `pluginpvp`).
- **name:** The friendly display name (e.g., `Plugin PvP`).

### /create-license
**Description:** Generates a new unique license key for a specific product.
- **product:** The ID of the product you want to generate the key for.

### /browse-customers
**Description:** Lists all active customers currently registered in the database and the products they own.

### /browse-licenses
**Description:** Lists all generated license keys, showing their current status (Active/Redeemed/Expired).

### /audit-setup
**Description:** Configures the security channel where the bot will send action logs and security alerts.

---

## Public Commands
These commands are available to all users or customers interacting with the bot.

### /browse-products
**Description:** Opens an interactive product management panel where users can see available software and details.

### /redeem
**Description:** Allows a customer to activate their license key and bind it to their account/hardware.
- **key:** The license key received (Format: `XXXXX-XXXXX-XXXXX-XXXXX`).

---

## Command Overview Table

| Command | Permission | Purpose |
| :--- | :--- | :--- |
| `/create-product` | Administrator | Register software in the DB |
| `/create-license` | Administrator | Generate a new serial key |
| `/browse-customers` | Administrator | List users and their products |
| `/browse-licenses` | Administrator | View all keys in the system |
| `/audit-setup` | Administrator | Set up security log channel |
| `/browse-products` | Public | View available products |
| `/redeem` | Public | Activate a license key |

---

:::caution[Developer Note]
The bot status is automatically set to `Playing: Server running` upon successful connection. If the commands do not appear in your Discord client immediately, try restarting your Discord or check if the **Bot Token** has the `applications.commands` scope enabled in the Developer Portal.
:::