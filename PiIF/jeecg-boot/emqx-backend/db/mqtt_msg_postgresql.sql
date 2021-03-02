--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.12
-- Dumped by pg_dump version 9.6.12

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: mqtt_msg; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mqtt_msg (
    id bigint NOT NULL,
    from_username character varying(64),
    from_client_id character varying(64),
    topic character varying(255),
    qos integer,
    retain integer,
    payload text,
    ts timestamp without time zone
);


ALTER TABLE public.mqtt_msg OWNER TO postgres;

--
-- Name: mqtt_msg_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.mqtt_msg_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mqtt_msg_id_seq OWNER TO postgres;

--
-- Name: mqtt_msg_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.mqtt_msg_id_seq OWNED BY public.mqtt_msg.id;


--
-- Name: mqtt_msg id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mqtt_msg ALTER COLUMN id SET DEFAULT nextval('public.mqtt_msg_id_seq'::regclass);


--
-- Data for Name: mqtt_msg; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.mqtt_msg (id, from_username, from_client_id, topic, qos, retain, payload, ts) VALUES (1, 'emqx', 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 0, 0, '{"id":"2","params":{"values":[{"t":1600756500035,"bbb.Random1":47.0,"aaa.Ramp1":95.0},{"t":1600756499042,"bbb.Random1":-9.0,"aaa.Ramp1":63.0},{"t":1600756502036,"bbb.Random1":-12.0,"aaa.Ramp1":91.0},{"t":1600756496041,"bbb.Random1":34.0,"aaa.Ramp1":35.0},{"t":1600756504041,"bbb.Random1":-16.0,"aaa.Ramp1":87.0},{"t":1600756498042,"bbb.Random1":-1.0,"aaa.Ramp1":99.0},{"t":1600756497037,"bbb.Random1":9.0,"aaa.Ramp1":67.0},{"t":1600756501036,"bbb.Random1":7.0,"aaa.Ramp1":59.0},{"t":1600756503039,"bbb.Random1":37.0,"aaa.Ramp1":55.0},{"t":1600756505039,"bbb.Random1":60.0,"aaa.Ramp1":51.0}],"timestamp":1600756505311},"version":"1.0"}', '2020-10-21 09:20:54.548');
INSERT INTO public.mqtt_msg (id, from_username, from_client_id, topic, qos, retain, payload, ts) VALUES (2, 'emqx', 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 0, 0, '{"id":"3","params":{"values":[{"t":1600756500035,"bbb.Random1":47.0,"aaa.Ramp1":95.0},{"t":1600756499042,"bbb.Random1":-9.0,"aaa.Ramp1":63.0},{"t":1600756502036,"bbb.Random1":-12.0,"aaa.Ramp1":91.0},{"t":1600756496041,"bbb.Random1":34.0,"aaa.Ramp1":35.0},{"t":1600756504041,"bbb.Random1":-16.0,"aaa.Ramp1":87.0},{"t":1600756498042,"bbb.Random1":-1.0,"aaa.Ramp1":99.0},{"t":1600756497037,"bbb.Random1":9.0,"aaa.Ramp1":67.0},{"t":1600756501036,"bbb.Random1":7.0,"aaa.Ramp1":59.0},{"t":1600756503039,"bbb.Random1":37.0,"aaa.Ramp1":55.0},{"t":1600756505039,"bbb.Random1":60.0,"aaa.Ramp1":51.0}],"timestamp":1600756505311},"version":"1.0"}', '2020-10-21 09:20:59.549');
INSERT INTO public.mqtt_msg (id, from_username, from_client_id, topic, qos, retain, payload, ts) VALUES (3, 'emqx', 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 0, 0, '{"id":"4","params":{"values":[{"t":1600756500035,"bbb.Random1":47.0,"aaa.Ramp1":95.0},{"t":1600756499042,"bbb.Random1":-9.0,"aaa.Ramp1":63.0},{"t":1600756502036,"bbb.Random1":-12.0,"aaa.Ramp1":91.0},{"t":1600756496041,"bbb.Random1":34.0,"aaa.Ramp1":35.0},{"t":1600756504041,"bbb.Random1":-16.0,"aaa.Ramp1":87.0},{"t":1600756498042,"bbb.Random1":-1.0,"aaa.Ramp1":99.0},{"t":1600756497037,"bbb.Random1":9.0,"aaa.Ramp1":67.0},{"t":1600756501036,"bbb.Random1":7.0,"aaa.Ramp1":59.0},{"t":1600756503039,"bbb.Random1":37.0,"aaa.Ramp1":55.0},{"t":1600756505039,"bbb.Random1":60.0,"aaa.Ramp1":51.0}],"timestamp":1600756505311},"version":"1.0"}', '2020-10-21 09:21:04.551');
INSERT INTO public.mqtt_msg (id, from_username, from_client_id, topic, qos, retain, payload, ts) VALUES (4, 'emqx', 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 0, 0, '{"id":"5","params":{"values":[{"t":1600756500035,"bbb.Random1":47.0,"aaa.Ramp1":95.0},{"t":1600756499042,"bbb.Random1":-9.0,"aaa.Ramp1":63.0},{"t":1600756502036,"bbb.Random1":-12.0,"aaa.Ramp1":91.0},{"t":1600756496041,"bbb.Random1":34.0,"aaa.Ramp1":35.0},{"t":1600756504041,"bbb.Random1":-16.0,"aaa.Ramp1":87.0},{"t":1600756498042,"bbb.Random1":-1.0,"aaa.Ramp1":99.0},{"t":1600756497037,"bbb.Random1":9.0,"aaa.Ramp1":67.0},{"t":1600756501036,"bbb.Random1":7.0,"aaa.Ramp1":59.0},{"t":1600756503039,"bbb.Random1":37.0,"aaa.Ramp1":55.0},{"t":1600756505039,"bbb.Random1":60.0,"aaa.Ramp1":51.0}],"timestamp":1600756505311},"version":"1.0"}', '2020-10-21 09:21:09.552');
INSERT INTO public.mqtt_msg (id, from_username, from_client_id, topic, qos, retain, payload, ts) VALUES (5, 'emqx', 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 0, 0, '{"id":"6","params":{"values":[{"t":1600756500035,"bbb.Random1":47.0,"aaa.Ramp1":95.0},{"t":1600756499042,"bbb.Random1":-9.0,"aaa.Ramp1":63.0},{"t":1600756502036,"bbb.Random1":-12.0,"aaa.Ramp1":91.0},{"t":1600756496041,"bbb.Random1":34.0,"aaa.Ramp1":35.0},{"t":1600756504041,"bbb.Random1":-16.0,"aaa.Ramp1":87.0},{"t":1600756498042,"bbb.Random1":-1.0,"aaa.Ramp1":99.0},{"t":1600756497037,"bbb.Random1":9.0,"aaa.Ramp1":67.0},{"t":1600756501036,"bbb.Random1":7.0,"aaa.Ramp1":59.0},{"t":1600756503039,"bbb.Random1":37.0,"aaa.Ramp1":55.0},{"t":1600756505039,"bbb.Random1":60.0,"aaa.Ramp1":51.0}],"timestamp":1600756505311},"version":"1.0"}', '2020-10-21 09:21:14.554');
INSERT INTO public.mqtt_msg (id, from_username, from_client_id, topic, qos, retain, payload, ts) VALUES (6, 'emqx', 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 0, 0, '{"id":"7","params":{"values":[{"t":1600756500035,"bbb.Random1":47.0,"aaa.Ramp1":95.0},{"t":1600756499042,"bbb.Random1":-9.0,"aaa.Ramp1":63.0},{"t":1600756502036,"bbb.Random1":-12.0,"aaa.Ramp1":91.0},{"t":1600756496041,"bbb.Random1":34.0,"aaa.Ramp1":35.0},{"t":1600756504041,"bbb.Random1":-16.0,"aaa.Ramp1":87.0},{"t":1600756498042,"bbb.Random1":-1.0,"aaa.Ramp1":99.0},{"t":1600756497037,"bbb.Random1":9.0,"aaa.Ramp1":67.0},{"t":1600756501036,"bbb.Random1":7.0,"aaa.Ramp1":59.0},{"t":1600756503039,"bbb.Random1":37.0,"aaa.Ramp1":55.0},{"t":1600756505039,"bbb.Random1":60.0,"aaa.Ramp1":51.0}],"timestamp":1600756505311},"version":"1.0"}', '2020-10-21 09:21:19.555');
INSERT INTO public.mqtt_msg (id, from_username, from_client_id, topic, qos, retain, payload, ts) VALUES (7, 'emqx', 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 0, 0, '{"id":"8","params":{"values":[{"t":1600756500035,"bbb.Random1":47.0,"aaa.Ramp1":95.0},{"t":1600756499042,"bbb.Random1":-9.0,"aaa.Ramp1":63.0},{"t":1600756502036,"bbb.Random1":-12.0,"aaa.Ramp1":91.0},{"t":1600756496041,"bbb.Random1":34.0,"aaa.Ramp1":35.0},{"t":1600756504041,"bbb.Random1":-16.0,"aaa.Ramp1":87.0},{"t":1600756498042,"bbb.Random1":-1.0,"aaa.Ramp1":99.0},{"t":1600756497037,"bbb.Random1":9.0,"aaa.Ramp1":67.0},{"t":1600756501036,"bbb.Random1":7.0,"aaa.Ramp1":59.0},{"t":1600756503039,"bbb.Random1":37.0,"aaa.Ramp1":55.0},{"t":1600756505039,"bbb.Random1":60.0,"aaa.Ramp1":51.0}],"timestamp":1600756505311},"version":"1.0"}', '2020-10-21 09:21:24.557');
INSERT INTO public.mqtt_msg (id, from_username, from_client_id, topic, qos, retain, payload, ts) VALUES (8, 'emqx', 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 0, 0, '{"id":"9","params":{"values":[{"t":1600756500035,"bbb.Random1":47.0,"aaa.Ramp1":95.0},{"t":1600756499042,"bbb.Random1":-9.0,"aaa.Ramp1":63.0},{"t":1600756502036,"bbb.Random1":-12.0,"aaa.Ramp1":91.0},{"t":1600756496041,"bbb.Random1":34.0,"aaa.Ramp1":35.0},{"t":1600756504041,"bbb.Random1":-16.0,"aaa.Ramp1":87.0},{"t":1600756498042,"bbb.Random1":-1.0,"aaa.Ramp1":99.0},{"t":1600756497037,"bbb.Random1":9.0,"aaa.Ramp1":67.0},{"t":1600756501036,"bbb.Random1":7.0,"aaa.Ramp1":59.0},{"t":1600756503039,"bbb.Random1":37.0,"aaa.Ramp1":55.0},{"t":1600756505039,"bbb.Random1":60.0,"aaa.Ramp1":51.0}],"timestamp":1600756505311},"version":"1.0"}', '2020-10-21 09:21:29.559');
INSERT INTO public.mqtt_msg (id, from_username, from_client_id, topic, qos, retain, payload, ts) VALUES (9, 'emqx', 'device_2c', 'iotgateway/2c9480ed71b4ee7f0171b4ee7fe20000', 0, 0, '{"id":"10","params":{"values":[{"t":1600756500035,"bbb.Random1":47.0,"aaa.Ramp1":95.0},{"t":1600756499042,"bbb.Random1":-9.0,"aaa.Ramp1":63.0},{"t":1600756502036,"bbb.Random1":-12.0,"aaa.Ramp1":91.0},{"t":1600756496041,"bbb.Random1":34.0,"aaa.Ramp1":35.0},{"t":1600756504041,"bbb.Random1":-16.0,"aaa.Ramp1":87.0},{"t":1600756498042,"bbb.Random1":-1.0,"aaa.Ramp1":99.0},{"t":1600756497037,"bbb.Random1":9.0,"aaa.Ramp1":67.0},{"t":1600756501036,"bbb.Random1":7.0,"aaa.Ramp1":59.0},{"t":1600756503039,"bbb.Random1":37.0,"aaa.Ramp1":55.0},{"t":1600756505039,"bbb.Random1":60.0,"aaa.Ramp1":51.0}],"timestamp":1600756505311},"version":"1.0"}', '2020-10-21 09:21:34.561');


--
-- Name: mqtt_msg_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.mqtt_msg_id_seq', 9, true);


--
-- Name: mqtt_msg mqtt_msg_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mqtt_msg
    ADD CONSTRAINT mqtt_msg_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

