# JavaEEReactive
Reactive patterns implemented in JavaEE

- Asynchronous servlet execution
- Asynchronous EJB method
- Asynchronous EJB observer
- Asynchronous REST service

Usual reactive patterns:
- async file operations
- async DB operations
- async call to an external resource (service)
- async event passing

Reactive manifesto:
- Responsive
- Resilient (face failure, achieved by replication, containment, isolation, delegation; client of a component does not need to handle its failures)
- Elatic (scalable, no central bottlenecks; sharding or replication)
- Message-driven and asynchronous, non-blocking
