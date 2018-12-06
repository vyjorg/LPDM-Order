CREATE SEQUENCE public.pay_id_seq;
CREATE TABLE public."payment"
(
    id integer DEFAULT nextval('pay_id_seq'::regclass) PRIMARY KEY NOT NULL,
    label VARCHAR (20) NOT NULL
);
CREATE UNIQUE INDEX payment_label_uindex ON public.payment (label);
INSERT INTO public."payment" (id, label) VALUES (1, 'BANKCARD');
INSERT INTO public."payment" (id, label) VALUES (2, 'PAYPAL');