CREATE SEQUENCE "public".tokens_id_seq START WITH 1 INCREMENT BY 1;

CREATE  TABLE "public".tokens (
                                 token_id            integer DEFAULT nextval('tokens_id_seq'::regclass) NOT NULL  ,
                                 user_name          varchar(255)    NOT NULL ,
                                 accessToken         varchar(255)    NOT NULL ,
                                 refreshToken         varchar(255)    ,
                                 created_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
                                 updated_at           timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL  ,
                                 CONSTRAINT tokens_pkey PRIMARY KEY ( token_id )
-- 	CONSTRAINT tokens_email_key UNIQUE ( email ),
-- 	CONSTRAINT tokens_user_name_key UNIQUE ( user_name )
);



CREATE TRIGGER update_updated_at_tokens BEFORE UPDATE ON "public".tokens FOR EACH ROW EXECUTE FUNCTION update_updated_at();