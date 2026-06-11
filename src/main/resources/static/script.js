/* ===================== ACTIVE NAV ON SCROLL ===================== */
const secoes = ['inicio', 'horarios', 'eventos', 'sobre', 'voluntario'];
const linksMenu = secoes.map(id => document.getElementById('nav-' + id));

function atualizarMenu() {
  let secaoAtual = secoes[0];
  secoes.forEach(id => {
    const el = document.getElementById(id);
    if (el && window.scrollY + 80 >= el.offsetTop) secaoAtual = id;
  });
  linksMenu.forEach((a, i) => {
    if (!a) return;
    a.classList.toggle('active', secoes[i] === secaoAtual);
  });
}
window.addEventListener('scroll', atualizarMenu, { passive: true });


/* ===================== FORMULÁRIO DE VOLUNTÁRIO (integrado com API) ===================== */
async function handleSubmit(event) {
  event.preventDefault();

  const btn = event.target.querySelector('.btn-submit');
  const form = event.target;

  // Coleta os dados do formulário
  const payload = {
    nomeCompleto:   form.nomeCompleto.value.trim(),
    email:          form.emailContato.value.trim(),
    telefone:       form.telefoneContato.value.trim(),
    areaInteresse:  form.areaInteresse.value,
    mensagem:       form.mensagemVoluntario.value.trim()
  };

  // Estado de carregando
  btn.disabled = true;
  btn.textContent = 'Enviando...';

  try {
    const response = await fetch('/api/voluntarios', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      const erro = await response.json().catch(() => null);
      throw new Error(erro?.message || 'Erro ao enviar inscrição');
    }

    // Sucesso
    btn.textContent = '✓ Inscrição enviada!';
    btn.style.background = '#22c55e';

    setTimeout(() => {
      btn.innerHTML = `
        <svg viewBox="0 0 24 24" style="width:16px;height:16px;stroke:#fff;fill:none;stroke-width:2">
          <line x1="22" y1="2" x2="11" y2="13"/>
          <polygon points="22 2 15 22 11 13 2 9 22 2"/>
        </svg>
        Enviar Inscrição
      `;
      btn.style.background = '';
      btn.disabled = false;
      form.reset();
    }, 3000);

  } catch (error) {
    btn.textContent = '✗ ' + error.message;
    btn.style.background = '#ef4444';

    setTimeout(() => {
      btn.innerHTML = `
        <svg viewBox="0 0 24 24" style="width:16px;height:16px;stroke:#fff;fill:none;stroke-width:2">
          <line x1="22" y1="2" x2="11" y2="13"/>
          <polygon points="22 2 15 22 11 13 2 9 22 2"/>
        </svg>
        Enviar Inscrição
      `;
      btn.style.background = '';
      btn.disabled = false;
    }, 3000);
  }
}