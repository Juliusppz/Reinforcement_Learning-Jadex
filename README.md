# Reinforcement Learning in Jadex Examples
In this repository are extensions of two Jadex examples, GarbageCollector and HunterPrey. In each example, one agent is altered so it learns to perform its role more efficiently using Reinforcement Learning. The learning progress accumulates during the run of a single simulation until it converges. It is implemented using a similar algorithm to my CartPole project (https://github.com/Juliusppz/Reinforcement_Learning-Gym_Cartpole). For this a directed graph is used to store information about the state space, which is updated due to observations of the agent.

For the examples to be run they have to be executed using the Jadex environment (e.g. by including its sources in the build path), which is available at https://www.activecomponents.org/index.html#/download.
