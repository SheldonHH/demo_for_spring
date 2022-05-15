```
CREATE ROLE server3;
CREATE DATABASE server3 WITH ENCODING = 'UTF8' OWNER = server3;
ALTER ROLE "server3" WITH LOGIN;
ALTER ROLE server3 WITH PASSWORD 'password';
```


```
\c server3
DROP TABLE IF EXISTS U_PERSON_DATA; 
CREATE TABLE U_PERSON_DATA (
	data_id UUID PRIMARY KEY,
	name VARCHAR ( 50 )  NOT NULL,
	u1 text[],
	u2 text[],
	verified boolean	
);
ALTER TABLE U_PERSON_DATA OWNER TO server3;
```


```
\c server1
CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);
ALTER TABLE public.flyway_schema_history OWNER TO server3;


\q
pg_dump -U postgres -t flyway_schema_history server3 | psql server1  -U postgres
pg_dump -U postgres -t flyway_schema_history server3 | psql server2  -U postgres
pg_dump -U postgres -t flyway_schema_history server3 | psql peer1  -U postgres
pg_dump -U postgres -t flyway_schema_history server3 | psql peer2  -U postgres
pg_dump -U postgres -t flyway_schema_history server3 | psql client1  -U postgres
```

