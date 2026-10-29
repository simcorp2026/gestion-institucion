import {useEffect, useState} from 'react';

const THRESHOLD = 7;
const COLORS = {
    success: '#2e9e5b',
    warning: '#f0a030',
    danger: '#d64545'
};

// Pide datos al backend y lanza error si algo falla
function get(url) {
    return fetch(url).then((response) => {
        if (!response.ok) throw new Error('Error ' + response.status + ' en ' + url);
        return response.json().catch(() => {
            throw new Error('Respuesta no válida en ' + url + ' (revisa el proxy de Vite)');
        });
    });
}

// Convierte {period:'2026-1', career:''} en "period=2026-1"
function toQuery(filters) {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
        if (value) params.append(key, value);
    });
    return params.toString();
}

// Semáforo: verde aprobado, naranja justo, rojo reprobado
function colorOf(score) {
    if (score >= THRESHOLD) return COLORS.success;
    if (score >= 5) return COLORS.warning;
    return COLORS.danger;
}

// Barra horizontal. value y max definen el ancho.
function Bar({value, max, color}) {
    const width = max > 0 ? Math.min(100, (value / max) * 100) : 0;
    return (
        <div className="bar">
            <div className="bar-fill" style={{width: width + '%', background: color}}/>
        </div>
    );
}

function Card({title, value, hint}) {
    return (
        <article className="card">
            <h2>{title}</h2>
            <strong>{value}</strong>
            {hint && <small>{hint}</small>}
        </article>
    );
}

function ErrorBox({message}) {
    if (!message) return null;
    return <div className="error">{message}</div>;
}

// ---------- VISTA 1: Dashboard institucional ----------
function Dashboard({filters, profile}) {
    const [summary, setSummary] = useState(null);
    const [subjects, setSubjects] = useState([]);
    const [careerSubjects, setCareerSubjects] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const q = toQuery(filters);
        setError('');
        Promise.all([
            get('/api/analytics/summary?' + q),
            get('/api/analytics/approval-by-subject?' + q),
            get('/api/analytics/approval-by-career-subject?' + q),
        ])
            .then(([sum, subj, careerSubj]) => {
                setSummary(sum);
                setSubjects(subj);
                setCareerSubjects(careerSubj);
            })
            .catch((e) => setError(e.message));
    }, [filters]);

    if (error) return <ErrorBox message={error}/>;
    if (!summary) return <p>Cargando...</p>;

    const careers = Object.entries(summary.averageByCareer);
    const generalAverage = careers.length
        ? (careers.reduce((sum, [, v]) => sum + v, 0) / careers.length).toFixed(2)
        : '--';
    const totalGrades = subjects.reduce((sum, s) => sum + s.total, 0);
    const totalApproved = subjects.reduce((sum, s) => sum + s.approved, 0);
    const approvalRate = totalGrades ? ((100 * totalApproved) / totalGrades).toFixed(1) + '%' : '--';

    const showCareerAverage = profile === 'authority';
    const showCareerSubject = profile === 'coordinator';
    const showSubjects = profile === 'teacher' || profile === 'authority';
    const showRisk = profile === 'teacher' || profile === 'coordinator';
    const showTop = profile === 'authority' || profile === 'coordinator';

    return (
        <>
            <section className="grid">
                <Card title="Estudiantes" value={summary.totalStudents}/>
                <Card title="Promedio general" value={generalAverage} hint="sobre 10"/>
                <Card title="Aprobación" value={approvalRate} hint={'nota mínima ' + THRESHOLD}/>
                <Card title="En riesgo" value={summary.atRiskStudents.length} hint="requieren atención"/>
            </section>

            {showCareerAverage && (
                <section className="panel">
                    <h2>Promedio por carrera</h2>
                    {careers.length === 0 && <p>Sin datos para estos filtros.</p>}
                    {careers.map(([name, value]) => (
                        <div className="row" key={name}>
                            <span className="label">{name}</span>
                            <Bar value={value} max={10} color={colorOf(value)}/>
                            <span className="number">{value.toFixed(2)}</span>
                        </div>
                    ))}
                </section>
            )}

            {showCareerSubject && (
                <section className="panel">
                    <h2>Aprobación por carrera y asignatura</h2>
                    {careerSubjects.length === 0 && <p>Sin datos para estos filtros.</p>}
                    {careerSubjects.map((r) => (
                        <div className="row" key={r.career + r.subject}>
                            <span className="label">{r.career} · {r.subject}</span>
                            <Bar value={r.approvalRate} max={100}
                                 color={r.approvalRate >= 70 ? '#2e9e5b' : r.approvalRate >= 50 ? COLORS.warning : COLORS.danger}/>
                            <span className="number">{r.approvalRate}%</span>
                        </div>
                    ))}
                </section>
            )}

            {showSubjects && (
                <section className="panel">
                    <h2>Aprobación por asignatura</h2>
                    {subjects.length === 0 && <p>Sin datos para estos filtros.</p>}
                    {subjects.map((r) => (
                        <div className="row" key={r.subject}>
                            <span className="label">{r.subject}</span>
                            <Bar value={r.approvalRate} max={100}
                                 color={r.approvalRate >= 70 ? COLORS.success : r.approvalRate >= 50 ? COLORS.warning : COLORS.danger}/>
                            <span className="number">{r.approvalRate}%</span>
                        </div>
                    ))}
                </section>
            )}

            {showTop && (
                <section className="panel">
                    <h2>Top 10 estudiantes</h2>
                    <div className="scroll">
                        <table>
                            <thead>
                            <tr>
                                <th>#</th>
                                <th>Nombre</th>
                                <th>Carrera</th>
                                <th>Promedio</th>
                            </tr>
                            </thead>
                            <tbody>
                            {summary.topStudents.map((s, i) => (
                                <tr key={s.studentId}>
                                    <td>{i + 1}</td>
                                    <td>{s.name}</td>
                                    <td>{s.career}</td>
                                    <td><span className="badge"
                                              style={{background: colorOf(s.average)}}>{s.average}</span></td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </section>
            )}

            {showRisk && (
                <section className="panel">
                    <h2>Estudiantes en riesgo académico</h2>
                    {summary.atRiskStudents.length === 0 && <p>Ningún estudiante en riesgo.</p>}
                    {summary.atRiskStudents.length > 0 && (
                        <div className="scroll">
                            <table>
                                <thead>
                                <tr>
                                    <th>Nombre</th>
                                    <th>Carrera</th>
                                    <th>Promedio</th>
                                    <th>Reprobadas</th>
                                </tr>
                                </thead>
                                <tbody>
                                {summary.atRiskStudents.map((s) => (
                                    <tr key={s.studentId}>
                                        <td>{s.name}</td>
                                        <td>{s.career}</td>
                                        <td><span className="badge"
                                                  style={{background: colorOf(s.average)}}>{s.average}</span></td>
                                        <td>{s.failedSubjects}</td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </section>
            )}
        </>
    );
}

// ---------- VISTA 2: Historial académico por estudiante ----------
function History() {
    const [students, setStudents] = useState([]);
    const [studentId, setStudentId] = useState('');
    const [rows, setRows] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        get('/api/academic/students').then(setStudents).catch((e) => setError(e.message));
    }, []);

    useEffect(() => {
        if (!studentId) {
            setRows([]);
            return;
        }
        get('/api/academic/students/' + studentId + '/history')
            .then(setRows)
            .catch((e) => setError(e.message));
    }, [studentId]);

    const average = rows.length
        ? (rows.reduce((sum, r) => sum + r.score, 0) / rows.length).toFixed(2)
        : '--';

    return (
        <section className="panel">
            <h2>Historial académico</h2>
            <ErrorBox message={error}/>
            <select value={studentId} onChange={(e) => setStudentId(e.target.value)}>
                <option value="">Selecciona un estudiante</option>
                {students.map((s) => (
                    <option key={s.id} value={s.id}>{s.firstName} {s.lastName} ({s.careerName})</option>
                ))}
            </select>

            {rows.length > 0 && (
                <p className="summary-line">
                    Promedio: <strong>{average}</strong> · Materias: <strong>{rows.length}</strong>
                </p>
            )}

            {rows.map((r) => (
                <div className="row" key={r.gradeId}>
                    <span className="label">{r.periodCode} · {r.subjectName}</span>
                    <Bar value={r.score} max={10} color={colorOf(r.score)}/>
                    <span className="number">{r.score}</span>
                </div>
            ))}
        </section>
    );
}

// ---------- VISTA 3: Reporte de asignaturas (aprobación / reprobación) ----------
function SubjectsReport({filters}) {
    const [rows, setRows] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        setError('');
        get('/api/analytics/approval-by-subject?' + toQuery(filters))
            .then(setRows)
            .catch((e) => setError(e.message));
    }, [filters]);

    return (
        <section className="panel">
            <h2>Aprobación y reprobación por asignatura</h2>
            <ErrorBox message={error}/>
            {rows.length === 0 && !error && <p>Sin datos para estos filtros.</p>}
            <div className="scroll">
                <table>
                    <thead>
                    <tr>
                        <th>Asignatura</th>
                        <th>Total</th>
                        <th>Aprobados</th>
                        <th>Reprobados</th>
                        <th>% aprobación</th>
                    </tr>
                    </thead>
                    <tbody>
                    {rows.map((r) => (
                        <tr key={r.subject}>
                            <td>{r.subject}</td>
                            <td>{r.total}</td>
                            <td>{r.approved}</td>
                            <td>{r.failed}</td>
                            <td>
                                <div className="row compact">
                                    <Bar value={r.approvalRate} max={100}
                                             color={r.approvalRate >= 70 ? COLORS.success : r.approvalRate >= 50 ? COLORS.warning : COLORS.danger}/>
                                    <span className="number">{r.approvalRate}%</span>
                                </div>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </section>
    );
}

// ---------- App principal ----------
export default function App() {
    const [view, setView] = useState('dashboard');
    const [profile, setProfile] = useState('authority');
    const [periods, setPeriods] = useState([]);
    const [careers, setCareers] = useState([]);
    const [subjects, setSubjects] = useState([]);
    const [filters, setFilters] = useState({period: '', career: '', subject: ''});
    const [error, setError] = useState('');

    // Carga las opciones de los filtros
    useEffect(() => {
        Promise.all([
            get('/api/academic/periods'),
            get('/api/academic/careers'),
            get('/api/academic/subjects'),
        ])
            .then(([p, c, s]) => {
                setPeriods(p);
                setCareers(c);
                setSubjects(s);
            })
            .catch((e) => setError('No se pudieron cargar los filtros: ' + e.message));
    }, []);

    function changeFilter(name) {
        return (event) => setFilters({...filters, [name]: event.target.value});
    }

    return (
        <main className="layout">
            <header className="hero">
                <h1>Academic Analytics Dashboard</h1>
                <p>Indicadores académicos institucionales</p>
            </header>

            <ErrorBox message={error}/>

            <nav className="tabs">
                <button className={view === 'dashboard' ? 'active' : ''}
                        onClick={() => setView('dashboard')}>Dashboard
                </button>
                <button className={view === 'history' ? 'active' : ''} onClick={() => setView('history')}>Historial
                </button>
                <button className={view === 'subjects' ? 'active' : ''}
                        onClick={() => setView('subjects')}>Asignaturas
                </button>
            </nav>

            {view !== 'history' && (
                <section className="filters">
                    <label>
                        Periodo
                        <select value={filters.period} onChange={changeFilter('period')}>
                            <option value="">Todos los periodos</option>
                            {periods.map((p) => <option key={p.id} value={p.code}>{p.name}</option>)}
                        </select>
                    </label>
                    <label>
                        Carrera
                        <select value={filters.career} onChange={changeFilter('career')}>
                            <option value="">Todas las carreras</option>
                            {careers.map((c) => <option key={c.id} value={c.code}>{c.name}</option>)}
                        </select>
                    </label>
                    <label>
                        Asignatura
                        <select value={filters.subject} onChange={changeFilter('subject')}>
                            <option value="">Todas las asignaturas</option>
                            {subjects.map((s) => <option key={s.id} value={s.code}>{s.name}</option>)}
                        </select>
                    </label>
                    {view === 'dashboard' && (
                        <label>
                            Perfil
                            <select value={profile} onChange={(e) => setProfile(e.target.value)}>
                                <option value="authority">Autoridad</option>
                                <option value="coordinator">Coordinador de carrera</option>
                                <option value="teacher">Docente</option>
                            </select>
                        </label>
                    )}
                </section>
            )}

            {view === 'dashboard' && <Dashboard filters={filters} profile={profile}/>}
            {view === 'history' && <History/>}
            {view === 'subjects' && <SubjectsReport filters={filters}/>}
        </main>
    );
}