/**
 * Carrossel Interativo da Seção Solução - Efficientia
 * Controla a exibição de 1 celular por vez em telas menores (<= 768px)
 * com animação de slide direcional, setas de navegação, loop infinito e dots.
 */

document.addEventListener('DOMContentLoaded', () => {
    // Menu Hamburguer Mobile (<= 768px)
    const menu = document.querySelector('.menu');
    const btnMenu = document.querySelector('.btn-menu-hamburguer');
    const menuLinks = document.querySelectorAll('.link-menu');

    if (menu && btnMenu) {
        btnMenu.addEventListener('click', (e) => {
            e.stopPropagation();
            const isOpen = menu.classList.toggle('open');
            btnMenu.setAttribute('aria-expanded', isOpen ? 'true' : 'false');
        });

        // Fecha o menu ao clicar em qualquer link
        menuLinks.forEach(link => {
            link.addEventListener('click', () => {
                menu.classList.remove('open');
                btnMenu.setAttribute('aria-expanded', 'false');
            });
        });

        // Fecha o menu ao clicar fora dele
        document.addEventListener('click', (e) => {
            if (!menu.contains(e.target)) {
                menu.classList.remove('open');
                btnMenu.setAttribute('aria-expanded', 'false');
            }
        });
    }

    const wrapper = document.querySelector('.carousel-solucao-wrapper');
    if (!wrapper) return;

    const cards = Array.from(wrapper.querySelectorAll('.card-solucao'));
    const btnPrev = wrapper.querySelector('.btn-prev');
    const btnNext = wrapper.querySelector('.btn-next');
    const dots = Array.from(document.querySelectorAll('.dot-solucao'));

    if (cards.length === 0) return;

    let currentIndex = 0;
    let isAnimating = false;
    const animationDuration = 400; // ms

    // Garante que o primeiro card esteja ativo inicialmente
    function initCarousel() {
        cards.forEach((card, i) => {
            if (i === currentIndex) {
                card.classList.add('active');
            } else {
                card.classList.remove('active', 'slide-in-right', 'slide-in-left', 'slide-out-left', 'slide-out-right');
            }
        });
        updateDots();
    }

    function updateDots() {
        dots.forEach((dot, i) => {
            if (i === currentIndex) {
                dot.classList.add('active');
            } else {
                dot.classList.remove('active');
            }
        });
    }

    function goToSlide(nextIndex, direction = 'next') {
        if (isAnimating || nextIndex === currentIndex) return;

        // Só executa o carrossel se a tela estiver em modo mobile/tablet (<= 768px)
        if (window.innerWidth > 768) return;

        isAnimating = true;

        const currentCard = cards[currentIndex];
        const nextCard = cards[nextIndex];

        // Remove classes residuais
        cards.forEach(c => {
            c.classList.remove('slide-in-right', 'slide-in-left', 'slide-out-left', 'slide-out-right');
        });

        // Aplica animação direcional
        if (direction === 'next') {
            currentCard.classList.add('slide-out-left');
            nextCard.classList.add('active', 'slide-in-right');
        } else {
            currentCard.classList.add('slide-out-right');
            nextCard.classList.add('active', 'slide-in-left');
        }

        currentIndex = nextIndex;
        updateDots();

        setTimeout(() => {
            currentCard.classList.remove('active', 'slide-out-left', 'slide-out-right');
            nextCard.classList.remove('slide-in-right', 'slide-in-left');
            isAnimating = false;
        }, animationDuration);
    }

    function nextSlide() {
        const nextIndex = (currentIndex + 1) % cards.length;
        goToSlide(nextIndex, 'next');
    }

    function prevSlide() {
        const prevIndex = (currentIndex - 1 + cards.length) % cards.length;
        goToSlide(prevIndex, 'prev');
    }

    // Event listeners dos botões de seta
    if (btnNext) {
        btnNext.addEventListener('click', (e) => {
            e.preventDefault();
            nextSlide();
        });
    }

    if (btnPrev) {
        btnPrev.addEventListener('click', (e) => {
            e.preventDefault();
            prevSlide();
        });
    }

    // Event listeners dos dots
    dots.forEach((dot, index) => {
        dot.addEventListener('click', (e) => {
            e.preventDefault();
            if (index === currentIndex) return;
            const direction = index > currentIndex ? 'next' : 'prev';
            goToSlide(index, direction);
        });
    });

    // Suporte a swipe no touch (telas touch/mobile)
    let touchStartX = 0;
    let touchEndX = 0;

    wrapper.addEventListener('touchstart', (e) => {
        touchStartX = e.changedTouches[0].screenX;
    }, { passive: true });

    wrapper.addEventListener('touchend', (e) => {
        touchEndX = e.changedTouches[0].screenX;
        handleSwipe();
    }, { passive: true });

    function handleSwipe() {
        const diff = touchStartX - touchEndX;
        const minSwipeDistance = 45; // pixels

        if (Math.abs(diff) > minSwipeDistance) {
            if (diff > 0) {
                // Deslizou para a esquerda -> próximo
                nextSlide();
            } else {
                // Deslizou para a direita -> anterior
                prevSlide();
            }
        }
    }

    // Reset ao redimensionar a janela
    window.addEventListener('resize', () => {
        if (window.innerWidth > 768) {
            // Em desktop, restaura a visibilidade normal de todos os cards
            cards.forEach(c => {
                c.classList.remove('slide-in-right', 'slide-in-left', 'slide-out-left', 'slide-out-right');
            });
        }
    });

    initCarousel();
});
