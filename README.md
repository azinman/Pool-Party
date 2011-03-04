Pool Party
==========

DESCRIPTION
-----------
Pool Party is a basic exception-resistant caching implementation of a database pool for Scala 2.8.
It tries to make full use of the language's features to provide a nice but simple API wrapping JDBC.
Because it is a tightly-coupled wrap, it is compatible with existing libraries that make use of
JDBC directly.

It's objects are:
  1. Be scala-friendly
  2. Cache everything except ResultSets
  3. Be "safe" & resistant to exceptions and dead connections, auto-reconnecting when needed.
  4. Good defaults
  5. KISS.


It is a basic pool implementation. Does not auto-release connections, or try to maintain some number
of connections. It provides a convenience method to start the minimum number of connections.

Connections are stored in a LRU such that the earliest used connection is pulled next.

It uses thread-local storage so that recursive database borrows reuse the same connection.

STATUS
------
It is considered currently in a beta state. It is being built for a production service currently
under construction. The API should be considered stable.

There are currently no unit tests. It would be a welcome contribution!


EXAMPLE USAGE
-------------
Using the pool is simple. Interact only with the object DatabasePool.

1. Register a connection factory using DatabasePool.registerConnectionFactory, passing a function
   that takes no parameters and returns a JDBC Connection object. An example Postgresql
   implementation is included.

2. Call DatabasePool.assureMinimumConnections to establish the minimum number of connections,
   as discovered in Configgy's database.min_conns parameter.

3. Borrow connections using DatabasePool.borrow as shown below:

<pre><code>
def listFromDb:Seq[String] = {
  DatabasePool.borrow { (conn) =>
    var s = conn.prepareStatement("select hello from world where a = ?") // Cached on second call
    s.setString(1, "my parameter") // Normal JDBC interaction
    val rs = s.executeQuery
    val results = mutable.Buffer[String]()
    while (rs.next) {
      results += rs.getString("hello")
    }
    otherDbUsingFunction() // It's DatabasePool.borrow will reuse the same thread-local connection
    rs.close // Happens automatically if omitted providing exception-robustness
    results.toSeq // Borrow is genericly typed to whatever the anonymous function returns
  }
}
</code></pre>


DEPENDENCIES
------------
Pool Party uses Configgy for logging and configuration parameters. It is included.

An example section in the configgy configuration file as expected:

<pre><code>
&lt;database&gt;
  host = "localhost"
  port = 5432
  user = "johndoe"
  password = "secretpassword"
  name = "databasename"
  min_conns = 10
  max_conns = 200
&lt;/database&gt;
</code></pre>

It also uses Scala Time's wrapper of JodaTime.


TODOS
-----

Keep track of recursion such that tracked ResultSets are only closed when the outermost borrowed
instance closes still open connections. An exception inside one function will close all other open
outstanding ResultSets, as all borrowed Connection objects are thread-local.


LICENSE
-------
Pool Party is under the MIT license; but please contribute pull requests back!

Copyright 2011 NetCapital LLC.
