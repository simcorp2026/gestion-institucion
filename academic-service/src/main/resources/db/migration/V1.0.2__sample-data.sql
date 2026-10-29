INSERT INTO career (code, name)
VALUES ('COMP', 'Computacion');
INSERT INTO career (code, name)
VALUES ('IND', 'Industrial');

INSERT INTO academic_period (code, name, active)
VALUES ('2026-1', 'Periodo 2026-1', TRUE);
INSERT INTO academic_period (code, name, active)
VALUES ('2025-2', 'Periodo 2025-2', FALSE);

INSERT INTO subject (code, name, credits)
VALUES ('PRG1', 'Programación I', 4);
INSERT INTO subject (code, name, credits)
VALUES ('BDD', 'Bases de Datos', 4);
INSERT INTO subject (code, name, credits)
VALUES ('MAT', 'Matemática Discreta', 3);
INSERT INTO subject (code, name, credits)
VALUES ('RED', 'Redes', 3);

INSERT INTO teacher (identification, first_name, last_name, email)
VALUES ('1800000001', 'Luis', 'Mora', 'lmora@uce.edu.ec');
INSERT INTO teacher (identification, first_name, last_name, email)
VALUES ('1800000002', 'Marta', 'Vega', 'mvega@uce.edu.ec');

INSERT INTO student (identification, first_name, last_name, email, career_id)
SELECT v.id, v.fn, v.ln, v.em, c.id
FROM (VALUES ('1700000001', 'Ana', 'Pérez', 'ana@uce.edu.ec', 'COMP'),
             ('1700000002', 'Bruno', 'Castro', 'bruno@uce.edu.ec', 'COMP'),
             ('1700000003', 'Carla', 'Ríos', 'carla@uce.edu.ec', 'COMP'),
             ('1700000004', 'Diego', 'Salas', 'diego@uce.edu.ec', 'COMP'),
             ('1700000005', 'Elena', 'Mena', 'elena@uce.edu.ec', 'COMP'),
             ('1700000006', 'Fabián', 'Loor', 'fabian@uce.edu.ec', 'COMP'),
             ('1700000007', 'Gina', 'Toro', 'gina@uce.edu.ec', 'IND'),
             ('1700000008', 'Hugo', 'Paz', 'hugo@uce.edu.ec', 'IND'),
             ('1700000009', 'Iván', 'Rea', 'ivan@uce.edu.ec', 'IND'),
             ('1700000010', 'Julia', 'Vera', 'julia@uce.edu.ec', 'IND'),
             ('1700000011', 'Kevin', 'Nieto', 'kevin@uce.edu.ec', 'IND'),
             ('1700000012', 'Lucía', 'Ochoa', 'lucia@uce.edu.ec', 'IND')) AS v(id, fn, ln, em, ccode)
         JOIN career c ON c.code = v.ccode;

-- Un curso "A" por asignatura y periodo
INSERT INTO course (subject_id, teacher_id, period_id, parallel)
SELECT s.id, t.id, p.id, 'A'
FROM subject s
         CROSS JOIN academic_period p
         JOIN teacher t ON t.identification = '1800000001';

-- Todos matriculados en todo + notas aleatorias entre 4 y 10
INSERT INTO enrollment (student_id, course_id)
SELECT s.id, c.id
FROM student s
         CROSS JOIN course c;

INSERT INTO grade (enrollment_id, score)
SELECT id, ROUND((RANDOM() * 6 + 4)::NUMERIC, 2)
FROM enrollment;
