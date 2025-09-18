# 🌍 PEPSE: Environmental Procedural Simulator

## Overview
PEPSE is a Java-based open-world simulation featuring a dynamic day-night cycle, procedural terrain, and interactive avatar control. It delivers a modular, extensible simulation environment designed with clean object-oriented architecture and event-driven patterns.

## Features
- 🌅 Realistic day-night cycle with smooth light transitions
- 🏞️ Procedural terrain generation using Perlin Noise
- 🕹️ Player avatar with movement, jumping, and energy management
- 🌳 Animated flora with leaves swaying and collectible fruits
- ☁️ Moving clouds and rain triggered by avatar actions
- 🔄 Infinite world generation with dynamic loading/unloading

## Skills & Design Patterns
- Java OOP with clear class responsibilities (GameObject, Terrain, Avatar)
- Factory methods for object creation
- Observer and Component patterns via callbacks & lambdas
- Event-driven architecture with scheduled tasks and transitions
- Functional programming: method references & lambda expressions
- Performance optimizations through layered collision and caching

## Running the Project
1. Clone the repo.
2. Install Java 11+ and DanoGameLab dependency.
3. Run `PepseGameManager` main class.
4. Use arrow keys to move, space to jump, ESC to exit.

## Future Enhancements
- Water and advanced weather mechanics
- Expanded avatar interactions
- Multiplayer support
- More complex flora and fauna
