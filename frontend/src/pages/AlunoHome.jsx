import { useEffect, useState } from 'react'
import { api } from '../api'
import Abas from '../components/Abas'
import Extrato from '../components/Extrato'
import AlunoForm from '../components/AlunoForm'

export default function AlunoHome({ sessao, onSair }) {
  const [aba, setAba] = useState('vantagens')
  const [vantagens, setVantagens] = useState([])
  const [saldo, setSaldo] = useState(null)
  const [perfil, setPerfil] = useState(null)
  const [msg, setMsg] = useState(null)
  const [versao, setVersao] = useState(0)
  const { token, id } = sessao

  useEffect(() => {
    api('/api/vantagens', { token }).then(setVantagens).catch(e => setMsg({ erro: e.message }))
    api(`/api/alunos/${id}`, { token }).then(a => { setPerfil(a); setSaldo(a.saldo) }).catch(() => {})
  }, [token, id, versao])

  async function resgatar(v) {
    if (!confirm(`Resgatar "${v.nome}" por ${v.custo} moedas?`)) return
    setMsg(null)
    try {
      const t = await api(`/api/alunos/${id}/resgatar-vantagem/${v.id}`, { method: 'POST', token })
      setMsg({ ok: `Resgate realizado! Cupom ${t.codigoCupom} enviado para seu e-mail.` })
      setVersao(versao + 1)
    } catch (err) {
      setMsg({ erro: err.message })
    }
  }

  async function excluirConta() {
    if (!confirm('Excluir sua conta definitivamente?')) return
    try {
      await api(`/api/alunos/${id}`, { method: 'DELETE', token })
      onSair()
    } catch (err) {
      alert(err.message)
    }
  }

  return (
    <>
      <p className="saldo">Seu saldo: <strong>{saldo ?? '…'}</strong> moedas</p>
      <Abas abas={[['vantagens', 'Vantagens'], ['extrato', 'Extrato'], ['perfil', 'Meu perfil']]} atual={aba} onTrocar={setAba} />

      {aba === 'vantagens' && (
        <>
          {msg?.ok && <p className="ok">{msg.ok}</p>}
          {msg?.erro && <p className="erro">{msg.erro}</p>}
          <div className="vitrine">
            {vantagens.map(v => (
              <article key={v.id} className="card vantagem">
                <img src={v.foto} alt={v.nome} />
                <h3>{v.nome}</h3>
                <p className="empresa">{v.empresa}</p>
                <p>{v.descricao}</p>
                <div className="linha">
                  <strong>{v.custo} moedas</strong>
                  <button onClick={() => resgatar(v)} disabled={saldo !== null && saldo < v.custo}>Resgatar</button>
                </div>
              </article>
            ))}
            {vantagens.length === 0 && <p>Nenhuma vantagem disponível.</p>}
          </div>
        </>
      )}

      {aba === 'extrato' && (
        <Extrato caminho={`/api/alunos/${id}/extrato`} token={token} tipoUsuario="ALUNO" versao={versao} onSaldo={setSaldo} />
      )}

      {aba === 'perfil' && perfil && (
        <div className="card">
          <AlunoForm aluno={perfil} token={token} onSalvo={setPerfil} />
          <hr />
          <button className="perigo" onClick={excluirConta}>Excluir minha conta</button>
        </div>
      )}
    </>
  )
}
