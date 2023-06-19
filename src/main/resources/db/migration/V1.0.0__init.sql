CREATE OR REPLACE FUNCTION "public".update_updated_at()
    RETURNS trigger
    LANGUAGE plpgsql
AS $function$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$function$
;

CREATE SEQUENCE "public".users_id_seq START WITH 1 INCREMENT BY 1;

CREATE  TABLE "public".users (
	user_id            integer DEFAULT nextval('users_id_seq'::regclass) NOT NULL  ,
	user_name          varchar(255)    ,
	email         varchar(255)    ,
	password         varchar(255)    ,
	role        varchar(255)    ,
	created_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	updated_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
	CONSTRAINT users_pkey PRIMARY KEY ( user_id )
-- 	CONSTRAINT users_email_key UNIQUE ( email ),
-- 	CONSTRAINT users_user_name_key UNIQUE ( user_name )
);



CREATE TRIGGER update_updated_at_workers BEFORE UPDATE ON "public".users FOR EACH ROW EXECUTE FUNCTION update_updated_at();