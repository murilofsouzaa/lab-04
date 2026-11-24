import { useEffect, useState } from 'react'
import { api } from '../api'

const ROTULO = { ENVIO: 'Envio', RESGATE: 'Resgate', CREDITO_SEMESTRAL: 'Crédito semestral' }

/** Extrato de qualquer tipo de usuário. `versao` força recarregar após uma operação. */
export default function Extrato({ caminho, token, tipoUsuario, versao, onSaldo }) {
  const [extrato, setExtrato] = useState(null)
  const [erro, setErro] = useState('')

  useEffect(() => {
    api(caminho, { token })
      .then(dados => { setExtrato(dados); onSaldo?.(dados.saldo) })
      .catch(e => setErro(e.message))
  }, [caminho, token, versao]) // eslint-disable-line react-hooks/exhaustive-deps

  if (erro) return <p className="erro">{erro}</p>
  if (!extrato) return <p>Carregando…</p>

  // sinal do valor do ponto de vista de quem consulta
  const sinal = t => {
    if (tipoUsuario === 'EMPRESA') return ''
    if (t.tipo === 'CREDITO_SEMESTRAL') return '+'
    if (t.tipo === 'RESGATE') return '−'
    return tipoUsuario === 'ALUNO' ? '+' : '−'
  }

  return (
    <div className="card">
      {extrato.saldo !== null && <p className="saldo">Saldo: <strong>{extrato.saldo}</strong> moedas</p>}
      {extrato.transacoes.length === 0 ? <p>Nenhuma transação.</p> : (
        <div className="tabela">
          <table>
            <thead>
              <tr><th>Data</th><th>Tipo</th><th>Valor</th><th>Detalhes</th></tr>
            </thead>
            <tbody>
              {extrato.transacoes.map(t => (
                <tr key={t.id}>
                  <td>{new Date(t.data).toLocaleString('pt-BR')}</td>
                  <td>{ROTULO[t.tipo]}</td>
                  <td className={sinal(t) === '+' ? 'mais' : sinal(t) === '−' ? 'menos' : ''}>{sinal(t)}{t.valor}</td>
                  <td>
                    {t.tipo === 'ENVIO' && (tipoUsuario === 'ALUNO' ? `De ${t.professor}` : `Para ${t.aluno}`)}
                    {t.tipo === 'ENVIO' && <> — “{t.motivo}”</>}
                    {t.tipo === 'RESGATE' && <>{t.vantagem}{tipoUsuario === 'EMPRESA' && ` — ${t.aluno}`} · cupom <code>{t.codigoCupom}</code></>}
                    {t.tipo === 'CREDITO_SEMESTRAL' && t.motivo}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
