/**
 * Efficientia - Animação 3D Interativa em Loop (home.jsp)
 * Estilo: Blueprint / Vector Line-Art Monocromático em Azul Claro (#7dd3fc / #00d9ff)
 * Tecnologia: Three.js (EdgesGeometry + LineBasicMaterial) + GSAP Timeline
 */
import * as THREE from 'three';

(() => {
  const container = document.getElementById('home3dCanvasContainer');
  if (!container) return;

  // Paleta monocromática azul claro (alto contraste com o fundo azul elétrico da home.jsp)
  const PALETTE = {
    primary: 0x7dd3fc,     // Sky Blue 300 - Contorno principal
    secondary: 0x38bdf8,   // Sky Blue 400 - Linhas estruturais e piso
    bright: 0x00d9ff,      // Ciano brilhante - Smartphone, dados, envelope e pulso
    accent: 0xe0f2fe       // Azul gelo quase branco - Destaques de tela
  };

  // Cena, Câmera e Renderizador transparente
  const scene = new THREE.Scene();

  const camera = new THREE.PerspectiveCamera(42, 1, 0.1, 100);
  const defaultCamPos = { x: 0, y: 1.6, z: 9.2 };
  const defaultLookAt = new THREE.Vector3(0, 0.4, 0);
  camera.position.set(defaultCamPos.x, defaultCamPos.y, defaultCamPos.z);
  camera.lookAt(defaultLookAt);

  const renderer = new THREE.WebGLRenderer({
    antialias: true,
    alpha: true,
    powerPreference: 'high-performance'
  });
  renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2));
  renderer.setClearColor(0x000000, 0);
  container.appendChild(renderer.domElement);

  // Grupo raiz para permitir leve parallax no mouse/flutuação contínua
  const worldRoot = new THREE.Group();
  scene.add(worldRoot);

  // Utilitário: Cria malha exclusivamente em contorno wireframe (EdgesGeometry)
  function createWireframe(geometry, color = PALETTE.primary, opacity = 1) {
    const edges = new THREE.EdgesGeometry(geometry, 12);
    geometry.dispose(); // Libera a geometria base pois só usamos as arestas
    const material = new THREE.LineBasicMaterial({
      color,
      transparent: true,
      opacity
    });
    const line = new THREE.LineSegments(edges, material);
    line.userData.baseOpacity = opacity;
    return line;
  }

  // Utilitário: Cria linhas customizadas a partir de pontos 3D
  function createLineFromPoints(points, color = PALETTE.primary, opacity = 1, closed = false) {
    const geometry = new THREE.BufferGeometry().setFromPoints(points);
    const material = new THREE.LineBasicMaterial({
      color,
      transparent: true,
      opacity
    });
    const line = closed
      ? new THREE.LineLoop(geometry, material)
      : new THREE.Line(geometry, material);
    line.userData.baseOpacity = opacity;
    return line;
  }

  // Utilitário: Define opacidade de todos os contornos de um grupo (respeitando baseOpacity)
  function setGroupOpacity(group, factor) {
    group.visible = factor > 0.005;
    group.traverse((child) => {
      if (child.material && typeof child.material.opacity === 'number') {
        const base = child.userData.baseOpacity !== undefined ? child.userData.baseOpacity : 1;
        child.material.opacity = base * factor;
      }
    });
  }

  // Utilitário: Constrói o personagem estilizado em wireframe (reutilizável)
  function createCharacterRig(color = PALETTE.primary) {
    const rig = new THREE.Group();

    // Cabeça (octaedro/esfera facetada estilo blueprint)
    const head = createWireframe(new THREE.OctahedronGeometry(0.42, 1), color, 0.95);
    head.position.set(0, 1.75, 0);
    rig.add(head);

    // Pescoço
    const neck = createWireframe(new THREE.CylinderGeometry(0.1, 0.12, 0.22, 6), color, 0.75);
    neck.position.set(0, 1.28, 0);
    rig.add(neck);

    // Tronco estilizado (trapézio/caixa chanfrada)
    const torso = createWireframe(new THREE.BoxGeometry(0.95, 1.25, 0.48), color, 0.92);
    torso.position.set(0, 0.52, 0);
    rig.add(torso);

    // Detalhe peitoral HUD
    const chestCore = createWireframe(new THREE.BoxGeometry(0.55, 0.45, 0.5), PALETTE.bright, 0.65);
    chestCore.position.set(0, 0.7, 0);
    rig.add(chestCore);

    // Braço Esquerdo (ombro + antebraço articulado)
    const leftShoulder = new THREE.Group();
    leftShoulder.position.set(-0.62, 1.05, 0);
    const leftUpperArm = createWireframe(new THREE.BoxGeometry(0.22, 0.65, 0.22), color, 0.9);
    leftUpperArm.position.set(0, -0.28, 0);
    leftShoulder.add(leftUpperArm);

    const leftElbow = new THREE.Group();
    leftElbow.position.set(0, -0.6, 0);
    const leftForearm = createWireframe(new THREE.BoxGeometry(0.2, 0.62, 0.2), color, 0.9);
    leftForearm.position.set(0, -0.26, 0);
    leftElbow.add(leftForearm);
    leftShoulder.add(leftElbow);
    rig.add(leftShoulder);

    // Braço Direito (ombro + antebraço articulado)
    const rightShoulder = new THREE.Group();
    rightShoulder.position.set(0.62, 1.05, 0);
    const rightUpperArm = createWireframe(new THREE.BoxGeometry(0.22, 0.65, 0.22), color, 0.9);
    rightUpperArm.position.set(0, -0.28, 0);
    rightShoulder.add(rightUpperArm);

    const rightElbow = new THREE.Group();
    rightElbow.position.set(0, -0.6, 0);
    const rightForearm = createWireframe(new THREE.BoxGeometry(0.2, 0.62, 0.2), color, 0.9);
    rightForearm.position.set(0, -0.26, 0);
    rightElbow.add(rightForearm);
    rightShoulder.add(rightElbow);
    rig.add(rightShoulder);

    // Base / Pernas simplificadas estilo pedestal holográfico
    const hip = createWireframe(new THREE.BoxGeometry(0.82, 0.26, 0.42), color, 0.8);
    hip.position.set(0, -0.24, 0);
    rig.add(hip);

    const leftLeg = createWireframe(new THREE.BoxGeometry(0.28, 1.05, 0.28), color, 0.8);
    leftLeg.position.set(-0.24, -0.92, 0);
    rig.add(leftLeg);

    const rightLeg = createWireframe(new THREE.BoxGeometry(0.28, 1.05, 0.28), color, 0.8);
    rightLeg.position.set(0.24, -0.92, 0);
    rig.add(rightLeg);

    return {
      rig,
      head,
      torso,
      leftShoulder,
      leftElbow,
      rightShoulder,
      rightElbow
    };
  }

  // ============================================================================
  // ATO 1: Transição do Papel (Prancheta) para o Digital (Smartphone Brilhante)
  // ============================================================================
  function criarAto1() {
    const group = new THREE.Group();

    // Base circular estilo blueprint sob o personagem
    const ringOuter = createWireframe(new THREE.CylinderGeometry(2.1, 2.1, 0.04, 24), PALETTE.secondary, 0.45);
    ringOuter.position.set(0, -1.48, 0);
    group.add(ringOuter);

    const ringInner = createWireframe(new THREE.CylinderGeometry(1.45, 1.45, 0.04, 16), PALETTE.bright, 0.35);
    ringInner.position.set(0, -1.46, 0);
    group.add(ringInner);

    // Personagem central levemente angulado
    const char = createCharacterRig(PALETTE.primary);
    char.rig.position.set(0, 0, 0);
    char.rig.rotation.y = 0.18;
    group.add(char.rig);

    // Objeto 1: Prancheta de papel (no braço esquerdo inicialmente)
    const clipboardGroup = new THREE.Group();
    const board = createWireframe(new THREE.BoxGeometry(0.62, 0.82, 0.04), PALETTE.secondary, 0.9);
    clipboardGroup.add(board);

    const clipTop = createWireframe(new THREE.BoxGeometry(0.26, 0.1, 0.07), PALETTE.primary, 0.95);
    clipTop.position.set(0, 0.38, 0.02);
    clipboardGroup.add(clipTop);

    // Linhas simulando texto no papel
    for (let i = 0; i < 4; i++) {
      const y = 0.18 - i * 0.14;
      const linePts = [
        new THREE.Vector3(-0.2, y, 0.03),
        new THREE.Vector3(0.2, y, 0.03)
      ];
      clipboardGroup.add(createLineFromPoints(linePts, PALETTE.secondary, 0.75));
    }

    clipboardGroup.position.set(-0.78, 0.32, 0.78);
    clipboardGroup.rotation.set(-0.45, 0.35, -0.15);
    group.add(clipboardGroup);

    // Objeto 2: Smartphone Brilhante (no lado direito, emerge com brilho ciano)
    const phoneGroup = new THREE.Group();
    const phoneBody = createWireframe(new THREE.BoxGeometry(0.44, 0.78, 0.05), PALETTE.bright, 1);
    phoneGroup.add(phoneBody);

    const phoneScreen = createWireframe(new THREE.PlaneGeometry(0.36, 0.64), PALETTE.accent, 0.95);
    phoneScreen.position.set(0, 0, 0.03);
    phoneGroup.add(phoneScreen);

    // Halo / Ondas de sinal digital ao redor do smartphone
    const glowRing1 = createWireframe(new THREE.RingGeometry(0.32, 0.36, 16), PALETTE.bright, 0.85);
    glowRing1.position.set(0, 0, -0.02);
    phoneGroup.add(glowRing1);

    const glowRing2 = createWireframe(new THREE.RingGeometry(0.48, 0.52, 16), PALETTE.primary, 0.55);
    glowRing2.position.set(0, 0, -0.04);
    phoneGroup.add(glowRing2);

    // Ícone de check/gráfico dentro da tela do smartphone
    const checkPts = [
      new THREE.Vector3(-0.1, -0.02, 0.04),
      new THREE.Vector3(-0.02, -0.1, 0.04),
      new THREE.Vector3(0.12, 0.1, 0.04)
    ];
    phoneGroup.add(createLineFromPoints(checkPts, PALETTE.accent, 1));

    phoneGroup.position.set(0.75, -0.4, 0.82);
    phoneGroup.scale.set(0.01, 0.01, 0.01);
    group.add(phoneGroup);

    // Pose inicial do Ato 1 (olhando para a prancheta)
    function resetPose() {
      char.head.rotation.set(0.18, -0.25, 0);
      char.leftShoulder.rotation.set(-0.75, 0.2, 0.15);
      char.leftElbow.rotation.set(-0.65, 0, 0);
      char.rightShoulder.rotation.set(-0.15, 0, -0.1);
      char.rightElbow.rotation.set(-0.2, 0, 0);

      clipboardGroup.position.set(-0.72, 0.38, 0.78);
      clipboardGroup.rotation.set(-0.45, 0.35, -0.15);
      clipboardGroup.scale.set(1, 1, 1);

      phoneGroup.position.set(0.72, -0.2, 0.85);
      phoneGroup.rotation.set(-0.3, -0.35, 0);
      phoneGroup.scale.set(0.001, 0.001, 0.001);
      glowRing1.scale.set(1, 1, 1);
      glowRing2.scale.set(1, 1, 1);
    }

    resetPose();

    return {
      group,
      char,
      clipboardGroup,
      phoneGroup,
      glowRing1,
      glowRing2,
      ringInner,
      resetPose
    };
  }

  // ============================================================================
  // ATO 2: O Caminhão na Estrada Infinita (Geometrias Primitivas & Movimento)
  // ============================================================================
  function criarAto2() {
    const group = new THREE.Group();
    group.rotation.y = -0.52;
    group.rotation.x = 0.14;

    // Grupo do caminhão (com suspensão elástica sutil)
    const truckGroup = new THREE.Group();
    truckGroup.position.set(0, -0.15, 0);
    group.add(truckGroup);

    // Chassi inferior longo (BoxGeometry)
    const chassis = createWireframe(new THREE.BoxGeometry(3.5, 0.22, 1.32), PALETTE.secondary, 0.9);
    chassis.position.set(0, -0.35, 0);
    truckGroup.add(chassis);

    // Caçamba / Baú de transporte pecuário (BoxGeometry) com grades laterais
    const cargoBox = createWireframe(new THREE.BoxGeometry(2.35, 1.38, 1.36), PALETTE.primary, 0.95);
    cargoBox.position.set(-0.55, 0.45, 0);
    truckGroup.add(cargoBox);

    // Divisórias horizontais e verticais da gaiola boiadeira (estilo blueprint)
    for (let i = -1; i <= 1; i++) {
      const slat = createWireframe(new THREE.BoxGeometry(2.37, 0.08, 1.38), PALETTE.bright, 0.55);
      slat.position.set(-0.55, 0.45 + i * 0.34, 0);
      truckGroup.add(slat);
    }
    for (let j = -1; j <= 1; j++) {
      const pillar = createWireframe(new THREE.BoxGeometry(0.06, 1.38, 1.38), PALETTE.secondary, 0.5);
      pillar.position.set(-0.55 + j * 0.72, 0.45, 0);
      truckGroup.add(pillar);
    }

    // Cabine do motorista (BoxGeometry)
    const cabin = createWireframe(new THREE.BoxGeometry(0.95, 1.05, 1.28), PALETTE.primary, 0.95);
    cabin.position.set(1.18, 0.28, 0);
    truckGroup.add(cabin);

    // Quebra-vento / Defletor aerodinâmico no teto da cabine (BoxGeometry)
    const roofDeflector = createWireframe(new THREE.BoxGeometry(0.55, 0.22, 1.18), PALETTE.secondary, 0.8);
    roofDeflector.position.set(1.02, 0.92, 0);
    roofDeflector.rotation.z = -0.22;
    truckGroup.add(roofDeflector);

    // Para-brisa luminoso na cabine
    const windshield = createWireframe(new THREE.BoxGeometry(0.06, 0.44, 1.08), PALETTE.bright, 0.95);
    windshield.position.set(1.66, 0.46, 0);
    truckGroup.add(windshield);

    // Para-choque dianteiro e faróis
    const bumper = createWireframe(new THREE.BoxGeometry(0.18, 0.24, 1.34), PALETTE.bright, 0.85);
    bumper.position.set(1.72, -0.28, 0);
    truckGroup.add(bumper);

    // Rodas (CylinderGeometry rotacionadas no eixo X + raios internos para evidenciar o giro)
    const wheels = [];
    const wheelPositions = [
      { x: -1.25, z: 0.68 },
      { x: -1.25, z: -0.68 },
      { x: -0.35, z: 0.68 },
      { x: -0.35, z: -0.68 },
      { x: 1.15, z: 0.68 },
      { x: 1.15, z: -0.68 }
    ];

    wheelPositions.forEach((pos) => {
      const wheelPivot = new THREE.Group();
      wheelPivot.position.set(pos.x, -0.52, pos.z);

      const tire = createWireframe(new THREE.CylinderGeometry(0.36, 0.36, 0.22, 10), PALETTE.primary, 0.95);
      tire.rotation.x = Math.PI / 2;
      wheelPivot.add(tire);

      const hub = createWireframe(new THREE.CylinderGeometry(0.15, 0.15, 0.24, 6), PALETTE.bright, 0.9);
      hub.rotation.x = Math.PI / 2;
      wheelPivot.add(hub);

      // Raios cruzados na face externa da roda para deixar a rotação nítida
      const sideOffset = pos.z > 0 ? 0.12 : -0.12;
      const spokeH = [new THREE.Vector3(-0.32, 0, sideOffset), new THREE.Vector3(0.32, 0, sideOffset)];
      const spokeV = [new THREE.Vector3(0, -0.32, sideOffset), new THREE.Vector3(0, 0.32, sideOffset)];
      wheelPivot.add(createLineFromPoints(spokeH, PALETTE.bright, 0.85));
      wheelPivot.add(createLineFromPoints(spokeV, PALETTE.bright, 0.85));

      truckGroup.add(wheelPivot);
      wheels.push(wheelPivot);
    });

    // Estrada infinita (bordas laterais + faixas tracejadas e balizas que se deslocam)
    const roadGroup = new THREE.Group();
    roadGroup.position.set(0, -0.9, 0);
    group.add(roadGroup);

    const leftEdgePts = [new THREE.Vector3(-4.6, 0, -1.45), new THREE.Vector3(4.6, 0, -1.45)];
    const rightEdgePts = [new THREE.Vector3(-4.6, 0, 1.45), new THREE.Vector3(4.6, 0, 1.45)];
    roadGroup.add(createLineFromPoints(leftEdgePts, PALETTE.secondary, 0.7));
    roadGroup.add(createLineFromPoints(rightEdgePts, PALETTE.secondary, 0.7));

    // Faixas centrais e marcadores laterais de pista em movimento
    const roadStripes = [];
    const stripeCount = 8;
    const roadSpan = 9.2;
    for (let i = 0; i < stripeCount; i++) {
      const stripe = createWireframe(new THREE.BoxGeometry(0.62, 0.02, 0.12), PALETTE.bright, 0.85);
      stripe.position.set(-4.6 + i * (roadSpan / stripeCount), 0, 0);
      roadGroup.add(stripe);
      roadStripes.push(stripe);
    }

    // Linhas de fluxo de ar / velocidade ao redor do caminhão
    const speedLines = [];
    const speedConfigs = [
      { y: 0.85, z: -0.95, len: 1.1 },
      { y: 0.35, z: 0.95, len: 1.4 },
      { y: 1.15, z: 0.45, len: 0.95 }
    ];
    speedConfigs.forEach((cfg, idx) => {
      const pts = [new THREE.Vector3(0, 0, 0), new THREE.Vector3(cfg.len, 0, 0)];
      const sLine = createLineFromPoints(pts, PALETTE.accent, 0.45);
      sLine.position.set(1.5 - idx * 1.4, cfg.y, cfg.z);
      group.add(sLine);
      speedLines.push(sLine);
    });

    // Atualização contínua por frame (rotação das rodas, deslocamento da estrada e suspensão)
    let lastTime = 0;
    function update(elapsed) {
      const dt = Math.min(Math.max(elapsed - lastTime, 0), 0.1);
      lastTime = elapsed;

      // Rotação das 6 rodas no sentido de avanço (+X)
      wheels.forEach((wheel) => {
        wheel.rotation.z -= dt * 8.5;
      });

      // Oscilação sutil do chassi/caçamba na estrada
      truckGroup.position.y = -0.15 + Math.sin(elapsed * 12) * 0.028;
      truckGroup.rotation.z = Math.cos(elapsed * 8.5) * 0.009;

      // Movimento infinito das faixas da estrada da frente(+X) para trás(-X)
      roadStripes.forEach((stripe) => {
        stripe.position.x -= dt * 4.8;
        if (stripe.position.x < -4.6) {
          stripe.position.x += roadSpan;
        }
      });

      // Movimento das linhas de velocidade
      speedLines.forEach((sLine) => {
        sLine.position.x -= dt * 6.2;
        if (sLine.position.x < -3.8) {
          sLine.position.x = 2.8;
        }
      });
    }

    group.visible = false;
    return {
      group,
      truckGroup,
      update
    };
  }

  // ============================================================================
  // ATO 3: O Envio e Recepção do Relatório/Email (Curva Parabólica ao Analista)
  // ============================================================================
  function criarAto3() {
    const group = new THREE.Group();
    group.rotation.y = -0.18;
    group.rotation.x = 0.12;

    // Helper interno: constrói estação de trabalho (mesa + monitor + base)
    function createWorkstation(isAnalyst = false) {
      const station = new THREE.Group();

      // Tampo da mesa
      const deskTop = createWireframe(new THREE.BoxGeometry(1.55, 0.08, 0.95), PALETTE.secondary, 0.85);
      deskTop.position.set(0, -0.35, 0);
      station.add(deskTop);

      // Pés da mesa
      const legOffsets = [
        [-0.68, -0.88, -0.38],
        [0.68, -0.88, -0.38],
        [-0.68, -0.88, 0.38],
        [0.68, -0.88, 0.38]
      ];
      legOffsets.forEach(([lx, ly, lz]) => {
        const leg = createWireframe(new THREE.BoxGeometry(0.07, 1.05, 0.07), PALETTE.secondary, 0.65);
        leg.position.set(lx, ly, lz);
        station.add(leg);
      });

      // Grupo do Monitor (para aplicar pulso de escala no analista)
      const monitorGroup = new THREE.Group();
      monitorGroup.position.set(0, 0.22, 0);
      station.add(monitorGroup);

      const screenFrame = createWireframe(
        new THREE.BoxGeometry(0.92, 0.62, 0.06),
        isAnalyst ? PALETTE.bright : PALETTE.primary,
        0.95
      );
      monitorGroup.add(screenFrame);

      const innerDisplay = createWireframe(
        new THREE.PlaneGeometry(0.78, 0.48),
        PALETTE.accent,
        0.75
      );
      innerDisplay.position.set(0, 0, 0.035);
      monitorGroup.add(innerDisplay);

      // Halo de pulso no monitor do analista
      const pulseHalo = createWireframe(new THREE.BoxGeometry(1.06, 0.76, 0.08), PALETTE.bright, 0.8);
      pulseHalo.scale.set(0.85, 0.85, 0.85);
      monitorGroup.add(pulseHalo);

      // Suporte do monitor
      const standNeck = createWireframe(new THREE.BoxGeometry(0.1, 0.26, 0.08), PALETTE.secondary, 0.85);
      standNeck.position.set(0, -0.2, 0);
      station.add(standNeck);

      const standBase = createWireframe(new THREE.BoxGeometry(0.42, 0.04, 0.28), PALETTE.secondary, 0.85);
      standBase.position.set(0, -0.31, 0);
      station.add(standBase);

      // Teclado
      const keyboard = createWireframe(new THREE.BoxGeometry(0.48, 0.03, 0.2), PALETTE.primary, 0.8);
      keyboard.position.set(0, -0.3, 0.26);
      station.add(keyboard);

      return { station, monitorGroup, pulseHalo };
    }

    // Estação Esquerda: Remetente (Operador / Motorista)
    const senderStation = createWorkstation(false);
    senderStation.station.position.set(-1.95, 0, 0.2);
    senderStation.station.rotation.y = 0.42;
    group.add(senderStation.station);

    // Personagem simplificado atrás da estação remetente
    const senderChar = createCharacterRig(PALETTE.primary);
    senderChar.rig.scale.set(0.68, 0.68, 0.68);
    senderChar.rig.position.set(-2.35, -0.25, -0.42);
    senderChar.rig.rotation.y = 0.42;
    senderChar.leftShoulder.rotation.x = -0.65;
    senderChar.rightShoulder.rotation.x = -0.65;
    group.add(senderChar.rig);

    // Estação Direita: Analista (Recepção de Relatórios)
    const analystStation = createWorkstation(true);
    analystStation.station.position.set(1.95, 0, -0.15);
    analystStation.station.rotation.y = -0.45;
    group.add(analystStation.station);

    // Personagem Analista atrás da mesa direita
    const analystChar = createCharacterRig(PALETTE.secondary);
    analystChar.rig.scale.set(0.68, 0.68, 0.68);
    analystChar.rig.position.set(2.35, -0.25, -0.78);
    analystChar.rig.rotation.y = -0.45;
    analystChar.leftShoulder.rotation.x = -0.55;
    analystChar.rightShoulder.rotation.x = -0.55;
    group.add(analystChar.rig);

    // Arco guia pontilhado conectando os dois monitores
    const flightCurve = new THREE.QuadraticBezierCurve3(
      new THREE.Vector3(-1.85, 0.35, 0.25),
      new THREE.Vector3(0, 1.85, 0.45),
      new THREE.Vector3(1.85, 0.32, -0.1)
    );
    const arcPoints = flightCurve.getPoints(28);
    const trajectoryLine = createLineFromPoints(arcPoints, PALETTE.secondary, 0.35);
    group.add(trajectoryLine);

    // Envelope Vetorial Estilizado (Retângulo wireframe + Aba triangular superior)
    const envelopeGroup = new THREE.Group();
    const envRect = createWireframe(new THREE.BoxGeometry(0.56, 0.36, 0.03), PALETTE.bright, 1);
    envelopeGroup.add(envRect);

    // Triângulo da aba do envelope (Vector Line-Art)
    const flapPoints = [
      new THREE.Vector3(-0.28, 0.18, 0.02),
      new THREE.Vector3(0, -0.04, 0.02),
      new THREE.Vector3(0.28, 0.18, 0.02)
    ];
    envelopeGroup.add(createLineFromPoints(flapPoints, PALETTE.accent, 1));

    // Linhas diagonais inferiores do envelope
    const bottomLeftFold = [
      new THREE.Vector3(-0.28, -0.18, 0.02),
      new THREE.Vector3(-0.08, -0.01, 0.02)
    ];
    const bottomRightFold = [
      new THREE.Vector3(0.28, -0.18, 0.02),
      new THREE.Vector3(0.08, -0.01, 0.02)
    ];
    envelopeGroup.add(createLineFromPoints(bottomLeftFold, PALETTE.primary, 0.85));
    envelopeGroup.add(createLineFromPoints(bottomRightFold, PALETTE.primary, 0.85));

    group.add(envelopeGroup);

    const flightState = { progress: 0 };

    function updateEnvelopePosition() {
      const pos = flightCurve.getPoint(flightState.progress);
      envelopeGroup.position.copy(pos);
      // Inclina o envelope acompanhando a tangente da parábola
      const tangent = flightCurve.getTangent(flightState.progress);
      envelopeGroup.rotation.z = Math.atan2(tangent.y, tangent.x) * 0.55;
      envelopeGroup.rotation.y = Math.sin(flightState.progress * Math.PI) * 0.35;
    }

    function resetAct3() {
      flightState.progress = 0;
      updateEnvelopePosition();
      envelopeGroup.scale.set(0.001, 0.001, 0.001);
      analystStation.monitorGroup.scale.set(1, 1, 1);
      analystStation.pulseHalo.scale.set(0.88, 0.88, 0.88);
    }

    resetAct3();
    group.visible = false;

    return {
      group,
      envelopeGroup,
      flightState,
      senderChar,
      analystStation,
      updateEnvelopePosition,
      resetAct3,
      update: (elapsed) => {
        // Animação sutil de digitação nos braços do remetente
        senderChar.leftElbow.rotation.x = -0.35 + Math.sin(elapsed * 14) * 0.12;
        senderChar.rightElbow.rotation.x = -0.35 + Math.cos(elapsed * 14) * 0.12;
      }
    };
  }

  // Instanciação única dos grupos das 3 cenas (evita vazamento de memória no loop)
  const ato1 = criarAto1();
  const ato2 = criarAto2();
  const ato3 = criarAto3();

  worldRoot.add(ato1.group);
  worldRoot.add(ato2.group);
  worldRoot.add(ato3.group);

  // Proxies de opacidade para transição suave via GSAP
  const fadeState = {
    ato1: 1,
    ato2: 0,
    ato3: 0
  };

  setGroupOpacity(ato1.group, 1);
  setGroupOpacity(ato2.group, 0);
  setGroupOpacity(ato3.group, 0);

  // ============================================================================
  // Máquina de Estados da Timeline do GSAP (Ato 1 -> Ato 2 -> Ato 3 em loop)
  // ============================================================================
  const gsapInstance = window.gsap;
  let masterTimeline = null;

  if (gsapInstance) {
    masterTimeline = gsapInstance.timeline({
      repeat: -1,
      defaults: { ease: 'power2.inOut' }
    });

    // --- ATO 1: Deixa a prancheta de papel e ergue o smartphone brilhante ---
    masterTimeline.call(() => {
      ato1.resetPose();
      setGroupOpacity(ato1.group, fadeState.ato1);
    });

    // 1.1 Personagem analisa a prancheta e decide descartá-la
    masterTimeline.to(ato1.char.head.rotation, { y: -0.35, x: 0.25, duration: 0.8 }, 'ato1_start');
    masterTimeline.to(ato1.clipboardGroup.position, { y: 0.48, duration: 0.8 }, 'ato1_start');

    // 1.2 Prancheta cai e encolhe enquanto braço esquerdo abaixa
    masterTimeline.to(ato1.clipboardGroup.position, {
      y: -1.35,
      x: -1.25,
      duration: 1.1,
      ease: 'back.in(1.4)'
    }, 'ato1_drop');
    masterTimeline.to(ato1.clipboardGroup.rotation, {
      x: -1.4,
      z: -0.8,
      duration: 1.1
    }, 'ato1_drop');
    masterTimeline.to(ato1.clipboardGroup.scale, {
      x: 0.001,
      y: 0.001,
      z: 0.001,
      duration: 0.9
    }, 'ato1_drop+=0.2');
    masterTimeline.to(ato1.char.leftShoulder.rotation, {
      x: -0.15,
      y: 0,
      z: 0.1,
      duration: 1.0
    }, 'ato1_drop');
    masterTimeline.to(ato1.char.leftElbow.rotation, {
      x: -0.2,
      duration: 1.0
    }, 'ato1_drop');

    // 1.3 Braço direito levanta e o Smartphone Brilhante surge com pulso holográfico
    masterTimeline.to(ato1.char.rightShoulder.rotation, {
      x: -0.85,
      y: -0.25,
      z: -0.15,
      duration: 1.1,
      ease: 'back.out(1.6)'
    }, 'ato1_phone');
    masterTimeline.to(ato1.char.rightElbow.rotation, {
      x: -0.75,
      duration: 1.1
    }, 'ato1_phone');
    masterTimeline.to(ato1.char.head.rotation, {
      y: 0.28,
      x: 0.12,
      duration: 1.0
    }, 'ato1_phone');
    masterTimeline.to(ato1.phoneGroup.position, {
      y: 0.62,
      x: 0.68,
      z: 0.88,
      duration: 1.1,
      ease: 'back.out(1.7)'
    }, 'ato1_phone');
    masterTimeline.to(ato1.phoneGroup.scale, {
      x: 1.15,
      y: 1.15,
      z: 1.15,
      duration: 1.1,
      ease: 'elastic.out(1, 0.6)'
    }, 'ato1_phone');

    // 1.4 Pulso luminoso nas ondas do smartphone
    masterTimeline.to(ato1.glowRing1.scale, {
      x: 1.45,
      y: 1.45,
      duration: 0.7,
      yoyo: true,
      repeat: 1
    }, 'ato1_pulse');
    masterTimeline.to(ato1.glowRing2.scale, {
      x: 1.65,
      y: 1.65,
      duration: 0.7,
      yoyo: true,
      repeat: 1
    }, 'ato1_pulse');

    // --- TRANSIÇÃO ATO 1 -> ATO 2 (Fade + Leve Panorâmica de Câmera) ---
    masterTimeline.to(fadeState, {
      ato1: 0,
      ato2: 1,
      duration: 1.0,
      onUpdate: () => {
        setGroupOpacity(ato1.group, fadeState.ato1);
        setGroupOpacity(ato2.group, fadeState.ato2);
      }
    }, 'transicao_1_2');

    masterTimeline.to(camera.position, {
      x: 0.45,
      y: 1.85,
      z: 8.8,
      duration: 1.2
    }, 'transicao_1_2');

    // --- ATO 2: Caminhão trafegando na estrada infinita (3.2s de exibição) ---
    masterTimeline.to(ato2.group.rotation, {
      y: -0.34,
      duration: 3.2,
      ease: 'sine.inOut'
    }, 'ato2_run');

    // --- TRANSIÇÃO ATO 2 -> ATO 3 ---
    masterTimeline.call(() => {
      ato3.resetAct3();
      ato2.group.rotation.y = -0.52;
    });

    masterTimeline.to(fadeState, {
      ato2: 0,
      ato3: 1,
      duration: 1.0,
      onUpdate: () => {
        setGroupOpacity(ato2.group, fadeState.ato2);
        setGroupOpacity(ato3.group, fadeState.ato3);
      }
    }, 'transicao_2_3');

    masterTimeline.to(camera.position, {
      x: 0,
      y: 1.7,
      z: 9.5,
      duration: 1.0
    }, 'transicao_2_3');

    // --- ATO 3: Voo parabólico do envelope e pulso no monitor do analista ---
    masterTimeline.to(ato3.envelopeGroup.scale, {
      x: 1,
      y: 1,
      z: 1,
      duration: 0.45,
      ease: 'back.out(1.8)'
    }, 'ato3_launch');

    masterTimeline.to(ato3.flightState, {
      progress: 1,
      duration: 1.85,
      ease: 'power1.inOut',
      onUpdate: () => {
        ato3.updateEnvelopePosition();
        setGroupOpacity(ato3.group, fadeState.ato3);
      }
    }, 'ato3_launch+=0.2');

    // Envelope entra no monitor do analista
    masterTimeline.to(ato3.envelopeGroup.scale, {
      x: 0.001,
      y: 0.001,
      z: 0.001,
      duration: 0.3,
      ease: 'power2.in'
    }, 'ato3_arrive');

    // Pulso de escala sutil no monitor do analista ao receber o relatório
    masterTimeline.to(ato3.analystStation.monitorGroup.scale, {
      x: 1.24,
      y: 1.24,
      z: 1.24,
      duration: 0.32,
      yoyo: true,
      repeat: 1,
      ease: 'power2.out'
    }, 'ato3_arrive+=0.1');

    masterTimeline.to(ato3.analystStation.pulseHalo.scale, {
      x: 1.48,
      y: 1.48,
      z: 1.48,
      duration: 0.55,
      yoyo: true,
      repeat: 1,
      ease: 'sine.out'
    }, 'ato3_arrive+=0.1');

    // --- FECHAMENTO DO LOOP: ATO 3 -> ATO 1 ---
    masterTimeline.call(() => {
      ato1.resetPose();
    });

    masterTimeline.to(fadeState, {
      ato3: 0,
      ato1: 1,
      duration: 1.0,
      onUpdate: () => {
        setGroupOpacity(ato3.group, fadeState.ato3);
        setGroupOpacity(ato1.group, fadeState.ato1);
      }
    }, 'transicao_3_1');

    masterTimeline.to(camera.position, {
      x: defaultCamPos.x,
      y: defaultCamPos.y,
      z: defaultCamPos.z,
      duration: 1.0
    }, 'transicao_3_1');
  }

  // ============================================================================
  // Responsividade e Pausa Inteligente quando Oculto em Telas Menores (<= 1100px)
  // ============================================================================
  const mediaQuery = window.matchMedia('(max-width: 1100px)');
  let isActive = !mediaQuery.matches;
  let rafId = null;
  const clock = new THREE.Clock();

  function updateSize() {
    if (mediaQuery.matches) {
      stopLoop();
      return;
    }

    const width = container.clientWidth;
    const height = container.clientHeight;
    if (width === 0 || height === 0) {
      stopLoop();
      return;
    }

    camera.aspect = width / height;
    camera.updateProjectionMatrix();
    renderer.setSize(width, height, false);

    if (!isActive) {
      startLoop();
    }
  }

  function renderFrame() {
    if (!isActive) return;
    rafId = window.requestAnimationFrame(renderFrame);

    const elapsed = clock.getElapsedTime();

    // Flutuação orgânica suave de todo o palco
    worldRoot.position.y = Math.sin(elapsed * 1.4) * 0.08;
    worldRoot.rotation.y = Math.sin(elapsed * 0.5) * 0.06;

    if (ato1.group.visible) {
      ato1.ringInner.rotation.y = elapsed * 0.4;
    }
    if (ato2.group.visible && ato2.update) {
      ato2.update(elapsed);
    }
    if (ato3.group.visible && ato3.update) {
      ato3.update(elapsed);
    }

    renderer.render(scene, camera);
  }

  function startLoop() {
    if (isActive && rafId !== null) return;
    isActive = true;
    clock.start();
    if (masterTimeline) masterTimeline.play();
    rafId = window.requestAnimationFrame(renderFrame);
  }

  function stopLoop() {
    isActive = false;
    if (rafId !== null) {
      window.cancelAnimationFrame(rafId);
      rafId = null;
    }
    if (masterTimeline) masterTimeline.pause();
  }

  // Observa redimensionamento do container e mudança de breakpoint
  const resizeObserver = new ResizeObserver(() => updateSize());
  resizeObserver.observe(container);
  mediaQuery.addEventListener('change', () => updateSize());
  window.addEventListener('resize', updateSize);

  // Inicializa tamanho e inicia loop caso a tela seja desktop (> 1100px)
  updateSize();
  if (!mediaQuery.matches && container.clientWidth > 0) {
    startLoop();
  }
})();
