import { useEffect, useState } from 'react'
import { api, lerSessao, salvarSessao } from './api'
import Login from './pages/Login'
import AlunoForm from './components/AlunoForm'
import EmpresaForm from './components/EmpresaForm'
import AlunoHome from './pages/AlunoHome'
import ProfessorHome from './pages/ProfessorHome'
import EmpresaHome from './pages/EmpresaHome'

const TITULO_TIPO = { ALUNO: 'Aluno', PROFESSOR: 'Professor', EMPRESA: 'Empresa parceira' }

export default function App() {
  const [sessao, setSessao] = useState(lerSessao)
  const [tela, setTela] = useState('login')
  const [aviso, setAviso] = useState('')

  useEffect(() => {
    const expirou = () => { salvarSessao(null); setSessao(null); setTela('login') }
    window.addEventListener('sessao-expirada', expirou)
    return () => window.removeEventListener('sessao-expirada', expirou)
  }, [])

  function entrar(dados) {
    salvarSessao(dados)
    setSessao(dados)
  }

  async function sair() {
    try { await api('/api/auth/logout', { method: 'POST', token: sessao.token }) } catch { /* sessão já inválida */ }
    salvarSessao(null)
    setSessao(null)
    setTela('login')
  }

  function cadastrado(msg) {
    setAviso(msg)
    setTela('login')
  }

  if (!sessao) {
    return (
      <main className="container estreito">
        <h1 className="marca">🪙 Moeda Estudantil</h1>
        {tela === 'login' && <Login onEntrar={entrar} onIr={setTela} aviso={aviso} />}
        {tela === 'cadastroAluno' && (
          <div className="card">
            <h2>Cadastro de aluno</h2>
            <AlunoForm onSalvo={() => cadastrado('Cadastro realizado. Faça login.')} />
            <button className="link" onClick={() => setTela('login')}>Voltar ao login</button>
          </div>
        )}
        {tela === 'cadastroEmpresa' && (
          <div className="card">
            <h2>Cadastro de empresa parceira</h2>
            <EmpresaForm onSalvo={() => cadastrado('Empresa cadastrada. Faça login.')} />
            <button className="link" onClick={() => setTela('login')}>Voltar ao login</button>
          </div>
        )}
      </main>
    )
  }

  return (
    <>
      <header className="topo">
        <span className="marca">🪙 Moeda Estudantil</span>
        <span>{sessao.nome} · {TITULO_TIPO[sessao.tipo]}</span>
        <button className="secundario" onClick={sair}>Sair</button>
      </header>
      <main className="container">
        {sessao.tipo === 'ALUNO' && <AlunoHome sessao={sessao} onSair={sair} />}
        {sessao.tipo === 'PROFESSOR' && <ProfessorHome sessao={sessao} />}
        {sessao.tipo === 'EMPRESA' && <EmpresaHome sessao={sessao} onSair={sair} />}
      </main>
    </>
  )
}
