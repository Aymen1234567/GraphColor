import java.io.*;
import java.util.*;

class Graph {
    int n;
    ArrayList<ArrayList<Integer>> adj;

    public Graph(int n) {
        this.n = n;
        adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }
}

public class Main {

    // Format DIMACS (.col) - fonction originale renommée
    public static Graph lireGrapheDIMACS(String nomFichier) {
        int maxSommet = 0;
        ArrayList<int[]> edges = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(nomFichier));
            String ligne;

            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.isEmpty() || ligne.startsWith("c")) continue;

                if (ligne.startsWith("p")) {
                    String[] parts = ligne.split("\\s+");
                    int n = Integer.parseInt(parts[2]);
                    maxSommet = n;
                } else if (ligne.startsWith("e")) {
                    String[] parts = ligne.split("\\s+");
                    int u = Integer.parseInt(parts[1]);
                    int v = Integer.parseInt(parts[2]);
                    edges.add(new int[]{u, v});
                    maxSommet = Math.max(maxSommet, Math.max(u, v));
                }
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Erreur lecture fichier");
            return null;
        }

        Graph G = new Graph(maxSommet);
        for (int[] e : edges) G.addEdge(e[0], e[1]);
        return G;
    }

    // Format SNAP (.txt)
    // Lignes de commentaire commencent par #
    // Chaque ligne : u v (séparés par espace ou tabulation)
    public static Graph lireGrapheSNAP(String nomFichier) {
        ArrayList<int[]> edges = new ArrayList<>();
        HashMap<Integer, Integer> remap = new HashMap<>();
        int compteur = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(nomFichier));
            String ligne;
            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.isEmpty() || ligne.startsWith("#")) continue;
                String[] parts = ligne.split("\\s+");
                if (parts.length < 2) continue;
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                if (!remap.containsKey(u)) remap.put(u, ++compteur);
                if (!remap.containsKey(v)) remap.put(v, ++compteur);
                edges.add(new int[]{remap.get(u), remap.get(v)});
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Erreur lecture fichier SNAP");
            return null;
        }

        Graph G = new Graph(compteur);
        HashSet<Long> vus = new HashSet<>();
        for (int[] e : edges) {
            if (e[0] == e[1]) continue;
            long cle = (long) Math.min(e[0], e[1]) * (compteur + 1) + Math.max(e[0], e[1]);
            if (vus.add(cle)) G.addEdge(e[0], e[1]);
        }
        return G;
    }

    // Format Konect (.tsv)
    // Lignes de commentaire commencent par %
    // Chaque ligne : u v (le poids éventuel est ignoré)
    public static Graph lireGrapheKonect(String nomFichier) {
        ArrayList<int[]> edges = new ArrayList<>();
        HashMap<Integer, Integer> remap = new HashMap<>();
        int compteur = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(nomFichier));
            String ligne;
            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.isEmpty() || ligne.startsWith("%")) continue;
                String[] parts = ligne.split("\\s+");
                if (parts.length < 2) continue;
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                if (!remap.containsKey(u)) remap.put(u, ++compteur);
                if (!remap.containsKey(v)) remap.put(v, ++compteur);
                edges.add(new int[]{remap.get(u), remap.get(v)});
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Erreur lecture fichier Konect");
            return null;
        }

        Graph G = new Graph(compteur);
        HashSet<Long> vus = new HashSet<>();
        for (int[] e : edges) {
            if (e[0] == e[1]) continue;
            long cle = (long) Math.min(e[0], e[1]) * (compteur + 1) + Math.max(e[0], e[1]);
            if (vus.add(cle)) G.addEdge(e[0], e[1]);
        }
        return G;
    }

    // Graphe aléatoire G(n, p)
    // Chaque paire (u,v) est reliée avec probabilité p
    public static Graph genererGrapheAleatoire(int n, double p, long seed) {
        Graph G = new Graph(n);
        Random rng = new Random(seed);
        for (int u = 1; u <= n; u++)
            for (int v = u + 1; v <= n; v++)
                if (rng.nextDouble() < p) G.addEdge(u, v);
        return G;
    }

    public static int[] dsatur(Graph G, int[] ni) {
        int n = G.n;
        int[] couleur = new int[n + 1];
        int[] dsat = new int[n + 1];
        int[] degre = new int[n + 1];

        for (int i = 1; i <= n; i++) degre[i] = G.adj.get(i).size();

        int nbColories = 0;
        int k = 0;

        while (true) {
            int vChoisi = -1;
            int maxDsat = -1;
            int maxDegre = -1;

            for (int v = 1; v <= n; v++) {
                if (couleur[v] == 0) {
                    if (dsat[v] > maxDsat ||
                        (dsat[v] == maxDsat && degre[v] > maxDegre) ||
                        (dsat[v] == maxDsat && degre[v] == maxDegre && vChoisi == -1)) {
                        vChoisi = v;
                        maxDsat = dsat[v];
                        maxDegre = degre[v];
                    }
                }
            }

            if (vChoisi == -1) break;

            boolean[] couleursVois = new boolean[n + 1];
            for (int voisin : G.adj.get(vChoisi))
                if (couleur[voisin] > 0) couleursVois[couleur[voisin]] = true;

            int c;
            for (c = 1; c <= n; c++)
                if (!couleursVois[c]) break;

            couleur[vChoisi] = c;
            nbColories++;
            if (ni[c] == 0) ni[c] = nbColories;
            k = Math.max(k, c);

            for (int voisin : G.adj.get(vChoisi)) {
                if (couleur[voisin] == 0) {
                    boolean[] couleursDistinctes = new boolean[n + 1];
                    int count = 0;
                    for (int u : G.adj.get(voisin)) {
                        int col = couleur[u];
                        if (col > 0 && !couleursDistinctes[col]) {
                            couleursDistinctes[col] = true;
                            count++;
                        }
                    }
                    dsat[voisin] = count;
                }
            }
        }

        return couleur;
    }
    public static int[] gloutonOrdreNaturel(Graph G, int[] ni) {
        int n = G.n;
        int[] couleur = new int[n + 1];
        int k = 0;

        for (int v = 1; v <= n; v++) {
            boolean[] couleursVois = new boolean[n + 1];
            for (int voisin : G.adj.get(v))
                if (couleur[voisin] > 0) couleursVois[couleur[voisin]] = true;

            int c;
            for (c = 1; c <= n; c++)
                if (!couleursVois[c]) break;

            couleur[v] = c;
            if (ni[c] == 0) ni[c] = v;
            k = Math.max(k, c);
        }

        return couleur;
    }
    private static void afficherComparaison(Graph G, String label, String sep, String fmt) {
        if (G == null) {
            System.out.printf(fmt + "%n", label, "ERR", "ERR", "ERR", "ERR", "ERR", "ERR", "ERR", "ERR");
        } else {
            int[] ni1 = new int[G.n + 1];
            int[] c1  = dsatur(G, ni1);
            int k1 = 0;
            for (int i = 1; i <= G.n; i++) if (c1[i] > k1) k1 = c1[i];
            double rho1 = (double) ni1[k1] / G.n;

            int[] ni2 = new int[G.n + 1];
            int[] c2  = gloutonOrdreNaturel(G, ni2);
            int k2 = 0;
            for (int i = 1; i <= G.n; i++) if (c2[i] > k2) k2 = c2[i];
            double rho2 = (double) ni2[k2] / G.n;

            System.out.printf(fmt + "%n", label,
                G.n, k1, ni1[k1], String.format("%.4f", rho1),
                G.n, k2, ni2[k2], String.format("%.4f", rho2));
        }
        System.out.println(sep);
    }
    public static void main(String[] args) {
        String[][] graphesReels = {
            {"DIMACS", "le450_15b.col",        "le450_15b"},
            {"Konect", "bio-CE-CX.edges",      "bio-CE-CX"},
            {"SNAP",   "twitter_combined.txt", "twitter"},
        };

        String sep = "+------------------------------+--------+-------+---------+-----------+--------+-------+---------+-----------+";
        String fmt = "| %-28s | %6s | %5s | %7s | %9s | %6s | %5s | %7s | %9s |";
        System.out.println(sep);
        System.out.printf(fmt + "%n", "", "DSatur", "", "", "", "Glouton", "", "", "");
        System.out.printf(fmt + "%n", "Graphe", "n", "k", "n_k", "ρ=n_k/n", "n", "k", "n_k", "ρ=n_k/n");
        System.out.println(sep);

        for (String[] g : graphesReels) {
            Graph G;
            switch (g[0]) {
                case "DIMACS": G = lireGrapheDIMACS(g[1]); break;
                case "Konect": G = lireGrapheKonect(g[1]); break;
                default:       G = lireGrapheSNAP(g[1]);   break;
            }
            afficherComparaison(G, g[2] + " (" + g[0] + ")", sep, fmt);
        }

        Graph G = genererGrapheAleatoire(500, 0.10, 42L);
        afficherComparaison(G, "G(500, 0.10) (Aléat.)", sep, fmt);
    }
}
