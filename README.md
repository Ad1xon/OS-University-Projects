# Operating Systems - University Projects

This repository contains practical implementations and simulations of fundamental Operating Systems concepts, developed as coursework assignments. All simulations are implemented in **Java**.

The primary focus of these projects is not only the implementation of the algorithms but also the development of robust simulation environments. These environments allow for parameterized testing, random data generation, and the collection of vital statistics to compare algorithm efficiency under various edge cases.

## Repository Structure 📁

The repository is organized by thematic lists, each covering a specific domain of operating systems resource management.

### [List 02: Disk Scheduling Algorithms](./List_02_Disk_Scheduling_Algorithms)
This project simulates how an operating system manages disk I/O requests. The simulation measures efficiency by tracking the total head movement distance across cylinders.

**Key Features:**
* **Standard Algorithms:** Implementation of **FCFS** (First Come First Serve), **SSTF** (Shortest Seek Time First), **SCAN** (elevator algorithm), and **C-SCAN** (circular SCAN).
* **Real-Time Strategies:** Implementation of priority-based scheduling with deadlines: **EDF** (Earliest Deadline First) and **FD-SCAN** (Feasible Deadline SCAN).
* **Parameterized Simulation:** Configurable disk size, number of real-time requests, and their distribution.

### [List 03: Page Replacement Algorithms](./List_03_Page_Replacement_Algorithms)
A simulation environment designed to compare the performance of various page replacement algorithms in a single-process context. The primary metric for evaluation is the number of **page faults**.

**Key Features:**
* **Locality of Reference:** The request sequence generator simulates the principle of locality, dividing execution into phases with restricted subsets of pages.
* **Implemented Algorithms:**
    * **FIFO** (First In First Out)
    * **OPT** (Optimal - theoretical baseline)
    * **LRU** (Least Recently Used)
    * **Approximated LRU** (Second Chance Algorithm using a reference bit)
    * **RAND** (Random replacement)
* **Variable Memory Sizes:** Testing across varying numbers of physical frames to observe performance scaling.

### [List 04: Frame Allocation Algorithms](./List_04_Frame_Allocation_Algorithms)
An extension of the page replacement project (List 03), moving from a single-process model to a multi-process environment. This project evaluates how physical memory frames are distributed among concurrently running processes and monitors for **thrashing**.

**Key Features:**
* **Global Request Sequence:** Handling simultaneous memory requests from multiple processes while maintaining disjoint page sets and individual locality of reference for each process.
* **Allocation Strategies:**
    * **Equal Allocation** (Static)
    * **Proportional Allocation** (Static, based on process size)
    * **Page Fault Frequency (PPF) Control** (Dynamic allocation based on fault rate thresholds)
    * **Working Set Model** (Dynamic allocation based on the working set size and a defined time window)
* **Thrashing Detection:** Mechanisms to detect when the system spends more time paging than executing, including strategies for process suspension.

## Technologies Used
* **Java**
