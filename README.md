<div align="center">

# Smart Traffic Lights Simulation

A CLI application that simulates an intelligent traffic light controller for a four-way intersection.  
The system dynamically selects traffic light phases based on current traffic demand and ensures safe vehicle movement through the intersection.

</div>

---

# Table of Contents

- [Project Goal](#project-goal)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Installation](#installation)
- [Running Web App](#running-web-app)
- [Input Format](#input-format)
- [Output Format](#output-format)
- [Intersection Model](#intersection-model)
- [Traffic Control Algorithm](#traffic-control-algorithm)
- [Example Simulation](#example-simulation)
- [Web Visualization](#web-visualization)
- [Project Assumptions](#project-assumptions)
- [Design Decisions](#design-decisions)
- [Tests](#tests)
- [Continuous Integration](#continuous-integration)

---

# Project Goal

The goal of this project is to simulate a traffic light controller for a four-way intersection.

The system processes a sequence of commands describing arriving vehicles and simulation steps.  
For every simulation step, the controller selects an appropriate traffic phase based on the current traffic situation and allows vehicles to pass safely through the intersection.

The simulation ensures:

- safe movement of vehicles (no conflicting directions)
- dynamic traffic phase selection based on demand
- tracking vehicles waiting on each road
- deterministic and reproducible simulation results

The output of the simulation is a JSON file containing vehicles that left the intersection after each simulation step.

---

[//]: # (# Tech Stack)

[//]: # (![Java17]&#40;https://img.shields.io/badge/-Java17-ffffff?style=flat-square&logo=openjdk&logoColor=000000&#41;)

[//]: # (![Maven]&#40;https://img.shields.io/badge/-ApacheMaven-C71A36?style=flat-square&logo=apachemaven&logoColor=000000&#41;)

[//]: # (![Jackson]&#40;https://img.shields.io/badge/-JacksonDatabind-FF9900?style=flat-square&#41;)

[//]: # (![JUnit5]&#40;https://img.shields.io/badge/-JUnit5-25A162?style=flat-square&logo=junit5&logoColor=000000&#41;)

# Tech Stack

### Backend

![Java17](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![SpringBoot](https://img.shields.io/badge/SpringBoot-3.x-green?logo=springboot)
![Maven](https://img.shields.io/badge/ApacheMaven-C71A36?logo=apachemaven)
![Jackson](https://img.shields.io/badge/Jackson-JSON-blue)

### Frontend

![Angular](https://img.shields.io/badge/Angular-17-red?logo=angular)
![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue?logo=typescript)
![RxJS](https://img.shields.io/badge/RxJS-reactive-purple)
![SVG](https://img.shields.io/badge/SVG-Visualization-orange)

### Testing

![JUnit5](https://img.shields.io/badge/JUnit5-testing-green?logo=junit5)
![Jasmine](https://img.shields.io/badge/Jasmine-tests-pink)
![Karma](https://img.shields.io/badge/Karma-test_runner-yellow)

### CI

![GitHubActions](https://img.shields.io/badge/GitHubActions-CI-blue?logo=githubactions)



# Architecture

The project is organized into several modules to keep responsibilities clearly separated.

- **engine** – simulation logic and traffic control algorithm
- **cli** – command line interface for running the simulation
- **server** – Spring Boot backend exposing the simulation as a REST API
- **web** – Angular frontend visualizing the intersection and simulation steps

---

# Installation

Clone the repository
```
git clone https://github.com/Meikelele/smart-traffic-lights.git
```
Go to the proper directory
```
cd smart-traffic-lights
```
Build the project
```
mvn clean package
```
Run the CLI application
```
java -jar cli/target/traffic-cli.jar samples/input_sample.json out/output.json
```

Arguments:
```
<input.json> input file containing simulation commands
<output.json> file where simulation result will be written
```

Example:
```
java -jar cli/target/traffic-cli.jar samples/input_sample.json out/output.json
```

---

# Running Web App

To start the web application

### Run a backend
```
mvn -pl server spring-boot:run
```

### Start a frontend
```
cd web/web
npm install
ng serve
```

Then open
```
http://localhost:4200
```

**Upload a simulation input JSON file and start the simulation.**


# Input Format

The simulation accepts a JSON file containing a list of commands.

Example:

```json
{
  "commands": [
    {
      "type": "addVehicle",
      "vehicleId": "vehicle1",
      "startRoad": "south",
      "endRoad": "north"
    },
    {
      "type": "step"
    }
  ]
}
```
### Supported Commands

The simulation processes two types of commands:

#### 1. `addVehicle`
Adds a new vehicle to the queue of a specified road.

| Field | Type | Description |
| :--- | :--- | :--- |
| **`type`** | `String` | Must be exactly `"addVehicle"`. |
| **`vehicleId`** | `String` | Unique identifier for the vehicle. |
| **`startRoad`** | `String` | The road where the vehicle enters the intersection. |
| **`endRoad`** | `String` | The road where the vehicle intends to exit the intersection. |

**Possible road values:** `north`, `south`, `east`, `west`.

#### 2. `step`
Executes one simulation tick.

- During this step, the traffic controller analyzes the current queues, selects the most appropriate traffic phase, and allows a batch of vehicles to pass safely through the intersection.
- This command requires only the `"type": "step"` field.

# Output Format

The simulation returns a JSON file describing vehicles that left the intersection in each step.

### Example

```json
{
  "stepStatuses": [
    {
      "leftVehicles": ["vehicle1", "vehicle2"]
    },
    {
      "leftVehicles": []
    },
    {
      "leftVehicles": ["vehicle3"]
    }
  ]
}
```
### Fields

- **`stepStatuses`** – list of simulation steps.
- **`leftVehicles`** – vehicles that passed through the intersection during a given step.

# Intersection Model

The simulated intersection contains four incoming roads.

![intersection_model.png](docs%2Fintersection_model.png)

### Key Principles

- Each road maintains a **FIFO queue** of waiting vehicles.
- Only the **first vehicle in each queue** may enter the intersection during a simulation step.
- Vehicles **cannot overtake** other vehicles in the queue.

# Traffic Control Algorithm

The traffic controller operates using **four traffic phases**.

### Phases

| Phase | Allowed movements |
| :--- | :--- |
| **`NS_THROUGH`** | north/south straight and right |
| **`NS_LEFT`** | north/south left turns |
| **`EW_THROUGH`** | east/west straight and right |
| **`EW_LEFT`** | east/west left turns |

### Simulation Step Execution

Each simulation step performs the following operations:

1. **Inspect** the first vehicle in each road queue.
2. **Calculate a score** for each phase based on vehicles that can pass during that phase.
3. **Select** the phase with the highest score.
4. **Allow** eligible vehicles to pass through the intersection.
5. **Remove** vehicles from their queues.
6. **Record** vehicles that left the intersection.

> To guarantee deterministic behavior, ties between phases are resolved using a fixed tie-breaking order.

# Example Simulation

The following example illustrates how the simulation processes commands step by step.

### Input

```json
{
  "commands": [
    {
      "type": "addVehicle",
      "vehicleId": "vehicle1",
      "startRoad": "south",
      "endRoad": "north"
    },
    {
      "type": "addVehicle",
      "vehicleId": "vehicle2",
      "startRoad": "north",
      "endRoad": "south"
    },
    {
      "type": "step"
    },
    {
      "type": "step"
    },
    {
      "type": "addVehicle",
      "vehicleId": "vehicle3",
      "startRoad": "west",
      "endRoad": "south"
    },
    {
      "type": "addVehicle",
      "vehicleId": "vehicle4",
      "startRoad": "west",
      "endRoad": "south"
    },
    {
      "type": "step"
    },
    {
      "type": "step"
    }
  ]
}
```

## Step-by-step Execution

### Step 1

Vehicles in queues:

| Road | Vehicles |
| :--- | :--- |
| **north** | `vehicle2` |
| **south** | `vehicle1` |
| **east** | — |
| **west** | — |
![step1.png](docs%2Fstep1.png)
- **Best phase:** `NS_THROUGH`
- **Vehicles that leave the intersection:** `vehicle1`, `vehicle2`

---

### Step 2

All queues are empty.

- **Best phase:** None / Default _(fixed NS_THROUGH)_
- **Vehicles that leave the intersection:** None `[]`

---

### Step 3

Vehicles in queues:

| Road | Vehicles |
| :--- | :--- |
| **north** | — |
| **south** | — |
| **east** | — |
| **west** | `vehicle3` |

`vehicle3` performs a **RIGHT** turn (**west → south**).
![step3.png](docs%2Fstep3.png)
- **Best phase:** `EW_THROUGH`
- **Vehicles that leave the intersection:** `vehicle3`

---

### Step 4

Vehicles in queues:

| Road | Vehicles |
| :--- | :--- |
| **north** | — |
| **south** | — |
| **east** | — |
| **west** | `vehicle4` |

`vehicle4` performs a **RIGHT** turn (**west → south**).
![step4.png](docs%2Fstep4.png)
- **Best phase:** `EW_THROUGH`
- **Vehicles that leave the intersection:** `vehicle4`

---

### Final Output

```json
{
  "stepStatuses": [
    {
      "leftVehicles": [
        "vehicle1",
        "vehicle2"
      ]
    },
    {
      "leftVehicles": []
    },
    {
      "leftVehicles": [
        "vehicle3"
      ]
    },
    {
      "leftVehicles": [
        "vehicle4"
      ]
    }
  ]
}
```

# Web Visualization

In addition to the CLI simulation, the project includes a **web visualization** built with Angular.

The web interface allows the user to:

- upload an `input.json` simulation file
- start, stop and reset the simulation
- visualize vehicles appearing on the intersection
- highlight the active traffic phase
- animate vehicles leaving the intersection
- inspect the simulation trace step by step

The intersection is rendered using **SVG**, which allows precise positioning of vehicles and traffic lanes.

### Example UI

![intersection_web_app_ui.png](docs%2Fintersection_web_app_ui.png)

# Project Assumptions

The simulation uses the following simplified traffic model:

- Vehicles are processed in **FIFO order**.
- **Maximum one vehicle** per road per step.
- Vehicles **cannot overtake** other vehicles in the queue.
- Left turns are only allowed in dedicated **LEFT phases**.
- Conflicting directions are separated into different phases.
- Traffic phase selection is based on **current demand**.

> This model ensures safety and deterministic behavior while keeping the simulation simple and efficient.

# Design Decisions

Several design decisions were made in order to keep the simulation deterministic, safe and easy to reason about.

### FIFO Vehicle Queues

Each incoming road maintains a FIFO queue of vehicles.

Reasons:

- reflects real-world traffic behavior
- prevents vehicles from overtaking
- simplifies the simulation logic
- guarantees deterministic vehicle ordering

---

### One Vehicle Per Road Per Step

During a simulation step, at most **one vehicle from each road** may leave the intersection.

Reasons:

- keeps the model simple and predictable
- avoids unrealistic simultaneous vehicle movements
- ensures consistent simulation results

---

### Head-of-Queue Evaluation

The algorithm evaluates only the **first vehicle in each queue** when scoring phases.

Reasons:

- vehicles deeper in the queue cannot move if the first vehicle is blocked
- reflects real-world queue behavior
- keeps phase evaluation efficient (constant-time checks)

---

### Phase-Based Traffic Control

Traffic movements are separated into four phases:

- `NS_THROUGH`
- `NS_LEFT`
- `EW_THROUGH`
- `EW_LEFT`

Reasons:

- prevents conflicting traffic movements
- separates left turns from through traffic
- simplifies safety guarantees

---

### Deterministic Tie-Breaking

When multiple phases receive the same score, a fixed tie-breaking order is used.

Reasons:

- guarantees deterministic simulation results
- simplifies debugging and testing
- avoids random phase switching

# Continuous Integration

The project uses **GitHub Actions** to automatically run tests on every push and pull request.

The CI pipeline performs:

1. Backend build and tests (Maven)
2. Frontend build and tests (Angular + Karma)

Example workflow:

```yaml
Backend tests
  mvn -B -ntp test

Frontend tests
  npm ci
  npm test -- --watch=false --browsers=ChromeHeadless

```

# Tests

The project contains a set of unit and integration-style tests that verify the correctness of the simulation logic, input parsing and validation rules.

The goal of the test suite is to ensure:

- correctness of vehicle movement logic
- deterministic simulation results
- proper handling of invalid input
- correct parsing of JSON commands

Tests are organized into three main categories.

---

## Unit Tests

Unit tests focus on small isolated pieces of logic.

### `TurnsTest`

Verifies correct computation of vehicle turn direction based on the start and end road.

Examples tested:

- straight movement
- left turns
- right turns

---

## Simulation Tests

Simulation tests verify the behavior of the traffic controller and vehicle queues.

### `SimulationGoldenTest`

Uses the example scenario provided in the task description to verify that the simulation produces the expected output.

This test acts as a **reference ("golden") scenario** for the whole system.

---

### `SimulationFifoBlockingTest`

Verifies that the simulation respects **FIFO queue ordering**.

If the first vehicle in a queue cannot move (for example due to an incompatible phase), vehicles behind it must also wait.

---

### `SimulationValidationTest`

Ensures proper validation of input data, such as:

- duplicate vehicle IDs
- invalid vehicle definitions
- incorrect command structure

---

## Parser Tests

Parser tests verify correct JSON input parsing.

### `SimulationInputParserTest`

Ensures that valid JSON input files are correctly parsed into internal command representations.

---

### `SimulationInputParserNegativeTest`

Verifies correct handling of invalid or malformed input data.

This includes scenarios such as:

- missing required fields
- invalid command types
- malformed JSON structure

---

## Test Strategy

Tests follow the **AAA pattern (Arrange – Act – Assert)**:

- **Arrange** – prepare input data
- **Act** – execute the tested logic
- **Assert** – verify expected results

---

## Running Tests

To execute the full test suite run:
```
mvn test
```
