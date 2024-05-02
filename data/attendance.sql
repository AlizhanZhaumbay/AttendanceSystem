create table attendance
(
    id        serial
        primary key,
    lesson_id integer
        constraint fkam01ddvne08oa3exny156v7al
            references lesson,
    date      timestamp(6),
    constraint unique_lesson
        unique (lesson_id, date)
);

alter table attendance
    owner to postgres;

create unique index attendance_date_lesson_id_idx
    on attendance (date, lesson_id);

INSERT INTO public.attendance (id, lesson_id, date) VALUES (1, 1, '2024-02-12 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (6, 2, '2024-02-12 10:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (11, 3, '2024-02-12 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (16, 4, '2024-02-13 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (21, 5, '2024-02-13 10:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (26, 6, '2024-02-13 13:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (31, 7, '2024-02-13 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (36, 14, '2024-02-13 12:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (41, 18, '2024-02-12 18:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (46, 17, '2024-02-14 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (51, 8, '2024-02-17 11:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (56, 15, '2024-02-14 16:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (61, 16, '2024-02-14 17:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (66, 9, '2024-02-17 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (71, 10, '2024-02-17 15:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (76, 19, '2024-02-16 11:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (80, 21, '2024-02-16 16:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (86, 22, '2024-02-16 17:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (91, 1, '2024-02-19 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (96, 2, '2024-02-19 10:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (101, 3, '2024-02-19 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (106, 4, '2024-02-20 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (111, 5, '2024-02-20 10:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (116, 6, '2024-02-20 13:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (121, 7, '2024-02-20 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (126, 14, '2024-02-20 12:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (131, 18, '2024-02-19 18:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (136, 17, '2024-02-21 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (141, 8, '2024-02-24 11:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (146, 15, '2024-02-21 16:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (151, 16, '2024-02-21 17:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (156, 9, '2024-02-24 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (161, 10, '2024-02-24 15:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (166, 19, '2024-02-23 11:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (170, 21, '2024-02-23 16:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (176, 22, '2024-02-23 17:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (181, 1, '2024-02-26 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (186, 2, '2024-02-26 10:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (191, 3, '2024-02-26 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (196, 4, '2024-02-27 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (201, 5, '2024-02-27 10:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (206, 6, '2024-02-27 13:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (211, 7, '2024-02-27 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (216, 14, '2024-02-27 12:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (221, 18, '2024-02-26 18:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (226, 17, '2024-02-28 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (231, 8, '2024-02-02 11:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (236, 15, '2024-02-28 16:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (241, 16, '2024-02-28 17:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (246, 9, '2024-03-02 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (251, 10, '2024-03-02 15:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (256, 19, '2024-03-01 11:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (261, 21, '2024-03-01 16:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (264, 21, '2024-03-01 17:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (266, 22, '2024-03-01 17:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (271, 1, '2024-03-04 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (276, 2, '2024-03-04 10:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (281, 3, '2024-03-04 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (286, 4, '2024-03-05 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (291, 5, '2024-03-05 10:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (296, 6, '2024-03-05 13:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (301, 7, '2024-03-05 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (306, 14, '2024-03-05 12:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (311, 18, '2024-03-04 18:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (316, 17, '2024-03-06 09:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (321, 8, '2024-03-09 11:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (326, 15, '2024-03-06 16:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (331, 16, '2024-03-06 17:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (336, 9, '2024-03-09 14:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (341, 10, '2024-03-09 15:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (346, 19, '2024-03-08 11:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (351, 21, '2024-03-08 16:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (354, 21, '2024-03-08 17:00:00.000000');
INSERT INTO public.attendance (id, lesson_id, date) VALUES (356, 22, '2024-03-08 17:00:00.000000');
