const secoes = ['inicio', 'horarios', 'eventos', 'sobre', 'voluntario'];

const linksMenu = secoes.map(function(id) {
  return document.getElementById('nav-' + id);
});

function atualizarMenu() {
  let secaoAtual = secoes[0];

  secoes.forEach(function(id) {
    const secao = document.getElementById(id);

    if (secao && window.scrollY + 80 >= secao.offsetTop) {
      secaoAtual = id;
    }
  });

  linksMenu.forEach(function(link, indice) {
    if (!link) return;

    if (secoes[indice] === secaoAtual) {
      link.classList.add('active');
    } else {
      link.classList.remove('active');
    }
  });
}

window.addEventListener('scroll', atualizarMenu, {
  passive: true
});

function handleSubmit(evento) {
  evento.preventDefault();

  const botao = evento.target.querySelector('.btn-submit');

  botao.textContent = '✓ Inscrição enviada!';
  botao.classList.add('enviado');
  botao.disabled = true;

  setTimeout(function() {
    botao.innerHTML = `
      <svg viewBox="0 0 24 24">
        <line x1="22" y1="2" x2="11" y2="13"/>
        <polygon points="22 2 15 22 11 13 2 9 22 2"/>
      </svg>
      Enviar Inscrição
    `;

    botao.classList.remove('enviado');
    botao.disabled = false;
    evento.target.reset();
  }, 3000);
}

    /* ---- Active nav on scroll ---- */
    const sections = ['inicio','horarios','eventos','sobre','voluntario'];
    const navLinks = sections.map(id => document.getElementById('nav-' + id));

    function updateNav() {
      let current = sections[0];
      sections.forEach(id => {
        const el = document.getElementById(id);
        if (el && window.scrollY + 80 >= el.offsetTop) current = id;
      });
      navLinks.forEach((a, i) => {
        if (!a) return;
        a.classList.toggle('active', sections[i] === current);
      });
    }
    window.addEventListener('scroll', updateNav, { passive: true });

    /* ---- Form submit ---- */
    function handleSubmit(e) {
      e.preventDefault();
      const btn = e.target.querySelector('.btn-submit');
      btn.textContent = '✓ Inscrição enviada!';
      btn.style.background = '#22c55e';
      btn.disabled = true;
      setTimeout(() => {
        btn.innerHTML = '<svg viewBox="0 0 24 24" style="width:16px;height:16px;stroke:#fff;fill:none;stroke-width:2"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg> Enviar Inscrição';
        btn.style.background = '';
        btn.disabled = false;
        e.target.reset();
      }, 3000);
    }