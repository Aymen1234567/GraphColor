# 🎨 GraphColor — Coloration de Graphes Réels en Java

> Licence L3 Informatique — Université Bourgogne Europe — Mai 2026
### Projet académique réaliser dans le cadre du cours graphes
---

## 📌 Description

**GraphColor** implémente et compare deux algorithmes heuristiques de coloration de graphes sur des instances réelles de grande taille :

- **DSatur** (Brélaz, 1979) — sélection dynamique par saturation
- **Glouton en ordre naturel** — parcours séquentiel des sommets

L'objectif est d'étudier la qualité des colorations produites et d'analyser le **ratio ρ = n_k / n**, qui mesure à quel moment de la coloration la dernière couleur est introduite.

---

## 🏗️ Structure du projet

```
projet_graphes/
├── Main.java               # Code source principal (tous algorithmes)
├── le450_15b.col           # Instance DIMACS (450 sommets, χ = 15)
├── bio-CE-CX.edges         # Réseau biologique Konect (15 229 sommets)
└── twitter_combined.txt    # Réseau social SNAP (81 306 sommets)
```

---

## ⚙️ Algorithmes implémentés

### DSatur — O(n²)
Heuristique gloutonne qui choisit à chaque étape le sommet non coloré ayant le plus grand nombre de couleurs distinctes parmi ses voisins déjà colorés (**saturation**). En cas d'égalité, on départage par degré puis par ordre naturel.

### Glouton ordre naturel — O(n + m)
Parcours des sommets dans l'ordre 1, 2, …, n. Chaque sommet reçoit la plus petite couleur non utilisée par ses voisins déjà colorés. Aucune sélection dynamique.

---

## 📂 Formats de fichiers supportés

| Format | Extension | Source | Notes |
|---|---|---|---|
| DIMACS | `.col` | DIMACS Challenge | Lignes `c` (commentaire), `p` (taille), `e` (arête) |
| SNAP | `.txt` | Stanford SNAP | Commentaires `#`, remapping automatique des sommets |
| Konect | `.edges` | Network Repository | Commentaires `%`, poids ignorés, remapping appliqué |
| Aléatoire G(n,p) | — | Généré | Graine fixe pour reproductibilité |

---

## 📊 Résultats expérimentaux

| Graphe | n | DSatur k | DSatur ρ | Glouton k | Glouton ρ |
|---|---|---|---|---|---|
| le450_15b (DIMACS) | 450 | 16 | 0.587 | 22 | 0.931 |
| bio-CE-CX (Konect) | 15 229 | 74 | 0.119 | 77 | 0.141 |
| twitter_combined (SNAP) | 81 306 | 71 | 0.585 | 77 | 0.608 |
| G(500, 0.10) (Aléatoire) | 500 | 16 | 0.746 | 18 | 0.954 |

> **ρ = n_k / n** : ratio indiquant à quel moment la dernière couleur est introduite (proche de 1 = tard, proche de 0 = tôt).

DSatur produit systématiquement de **meilleures colorations** que le glouton, au prix d'une complexité plus élevée.

---

## ▶️ Compilation et exécution

### Prérequis
- **Java JDK 8+**

### Compiler
```bash
javac Main.java
```

### Lancer
```bash
java Main
```

Le programme teste automatiquement les graphes disponibles et affiche pour chacun le nombre de couleurs utilisées, le ratio ρ, et le temps d'exécution.

---

## 📚 Concepts clés

- **Nombre chromatique χ(G)** : nombre minimal de couleurs pour colorier un graphe (NP-difficile à calculer exactement).
- **Saturation** : nombre de couleurs distinctes déjà présentes dans le voisinage d'un sommet.
- **Ratio ρ** : un faible ratio révèle une structure hétérogène (hubs très connectés qui forcent de nouvelles couleurs tôt) ; un ratio élevé traduit une structure plus homogène.

---

## 🔗 Sources des données

- DIMACS : https://mat.tepper.cmu.edu/COLOR/instances.html
- SNAP : http://snap.stanford.edu/data/index.html
- Konect : https://networkrepository.com/bio.php

---

## 👥 Auteurs

| Nom | Prénom |
|---|---|
| BERRAMDANI | Aymen |

---

## 📄 Licence

Projet académique — Université Bourgogne Europe — tous droits réservés aux auteurs.
