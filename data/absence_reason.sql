create table absence_reason
(
    id             bigserial
        primary key,
    description    varchar(255),
    status         varchar(255),
    file_path      varchar(255),
    requested_date timestamp(6),
    reason         varchar
);

alter table absence_reason
    owner to postgres;

INSERT INTO public.absence_reason (id, description, status, file_path, requested_date, reason) VALUES (10, null, 'APPROVED', 'https://attendance-system-appeals.s3.amazonaws.com/appeals/5bec58ec-d274-4018-ab4e-45478b1231a2', '2024-05-05 07:31:19.897141', 'My car got stuck in the snow because there was a snow storm the previous day.');
INSERT INTO public.absence_reason (id, description, status, file_path, requested_date, reason) VALUES (11, null, 'APPROVED', 'https://attendance-system-appeals.s3.amazonaws.com/appeals/b08ee3ab-18ac-4d78-834f-dfc40d449083', '2024-05-05 07:39:18.121495', 'i was not informed that there was a lecture today.');
INSERT INTO public.absence_reason (id, description, status, file_path, requested_date, reason) VALUES (9, null, 'DENIED', 'https://attendance-system-appeals.s3.amazonaws.com/appeals/982edc63-627d-4804-8871-38b41ab2891b', '2024-05-05 02:18:00.906799', 'I was sick');
