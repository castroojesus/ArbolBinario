import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable,KeyListener{
	
	final int screenWidth = 900;
    final int screenHeight = 700;
	
	 private enum Estado { MENU_PRINCIPAL, AYUDA, OPERACIONES, INFO_ARBOL }
	 private Estado estadoActual = Estado.MENU_PRINCIPAL;
	 private final String[] menuPrincipal = {
		        "╔══════════════════════════════════════╗",
		        "║ SISTEMA DE ARCHIVOS - v1.0      ║",
		        "╚══════════════════════════════════════╝",
		        "",
		        "  ▸ 1. Operar con el árbol",
		        "  ▸ 2. Ver información del árbol",
		        "  ▸ 3. Menú de ayuda y comandos",
		        "  ▸ 4. Exportar/Importar datos",
		        "  ▸ 5. Acerca del proyecto",
		        "  ▸ 0. Salir del programa",
		        "",
		        "──────────────────────────────────────",
		        "  [F1] Ayuda rápida   [ESC] Salir",
		        ""
		    };
	 
	 private final String[] menuAyuda = {
		        "╔══════════════════════════════════════════╗",
		        "║        MENÚ DE AYUDA              ║",
		        "╚══════════════════════════════════════════╝",
		        "",
		        "COMANDOS DEL SISTEMA DE ARCHIVOS:",
		        "──────────────────────────────────────",
		        "  • mkdir <padre> <nombre> <tipo> [contenido]",
		        "      → Añade archivo/carpeta al árbol",
		        "      Ej: add / Documentos carpeta",
		        "      Ej: add Documentos notas.txt archivo \"texto\"",
		        "",
		        "  • rm <nombre>",
		        "      → Elimina elemento (va a papelera)",
		        "",
		        "  • mv <origen> <destino>",
		        "      → Mueve elemento entre carpetas",
		        "",
		        "  • rename <viejo> <nuevo>",
		        "      → Cambia nombre de elemento",
		        "",
		        "  • find <nombre>",
		        "      → Busca elemento por nombre",
		        "",
		        "  • tree",
		        "      → Muestra estructura completa",
		        "",
		        "  • size",
		        "      → Muestra tamaño del árbol",
		        "",
		        "  • height",
		        "      → Muestra altura del árbol",
		        "",
		        "COMANDOS DE PERSISTENCIA:",
		        "──────────────────────────────────────",
		        "  • export <archivo.json>",
		        "      → Guarda árbol en formato JSON",
		        "",
		        "  • import <archivo.json>",
		        "      → Carga árbol desde JSON",
		        "",
		        "  • backup",
		        "      → Crea copia de seguridad automática",
		        "",
		        "CONTROLES DE INTERFAZ:",
		        "──────────────────────────────────────",
		        "  • F1          → Este menú de ayuda",
		        "  • ESC         → Salir/Volver atrás",
		        "  • TAB         → Cambiar entre paneles",
		        "  • F5          → Actualizar vista",
		        "  • CTRL+S      → Guardar rápidamente",
		        "  • CTRL+L      → Cargar último archivo",
		        "",
		        "CONCEPTOS IMPORTANTES:",
		        "──────────────────────────────────────",
		        "  • Árbol: Estructura jerárquica de carpetas/archivos",
		        "  • Nodo: Elemento del árbol (archivo o carpeta)",
		        "  • Raíz: Nodo principal del sistema (/)",
		        "  • JSON: Formato para guardar/recuperar datos",
		        "  • Trie: Estructura para búsqueda eficiente",
		        "",
		        "NOTAS:",
		        "──────────────────────────────────────",
		        "  • Los nombres deben ser únicos en cada carpeta",
		        "  • Solo las carpetas pueden contener elementos",
		        "  • La raíz no se puede eliminar",
		        "  • Use comillas para contenido con espacios",
		        "",
		        "──────────────────────────────────────",
		        "  [ESC] Volver al menú principal",
		        ""
		    };
	    
	   
	 private String mensajeEstado = "Sistema listo. Presiona F1 para ayuda.";
    
    private Arbol arbol;
	
	Thread gameThread;
	
	public GamePanel(Arbol arbol) {
		this.arbol=arbol;
		
		this.setPreferredSize(new Dimension(screenWidth,screenHeight));
		this.setBackground(new Color(0, 20, 40));
		this.setDoubleBuffered(true);
		this.setFocusable(true);
        this.addKeyListener(this);
	}
	
	public void startGameThread() {
		gameThread=new Thread(this);
		gameThread.start();
	}
	
	@Override
	public void run() {
		
		while(gameThread!=null) {
			 	
	            
	            
	            repaint();
	            
	            
	            try {
	                Thread.sleep(1000/60); // ~60 FPS
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
		}
	}
	
	 
	 
	 @Override
	    protected void paintComponent(Graphics g) {
		 super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D) g;
	        
	        
	        GradientPaint gradient = new GradientPaint(
	            0, 0, new Color(0, 10, 30),
	            0, screenHeight, new Color(0, 30, 60)
	        );
	        g2.setPaint(gradient);
	        g2.fillRect(0, 0, screenWidth, screenHeight);
	        
	        
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
	                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	        
	        
	        switch (estadoActual) {
	            case MENU_PRINCIPAL:
	                dibujarMenuPrincipal(g2);
	                break;
	            case AYUDA:
	                dibujarTexto(g2, menuAyuda, Color.CYAN);
	                break;
	            case INFO_ARBOL:
	                //dibujarInfoArbol(g2);
	                break;
	            case OPERACIONES:
	                //dibujarTexto(g2, infoPersistencia, Color.ORANGE);
	                break;
	        }
	        
	        
	        dibujarBarraEstado(g2);
	        
	        g2.dispose();
	    }
	 
	 private void dibujarMenuPrincipal(Graphics2D g2) {
	       
	        g2.setColor(new Color(100, 200, 255));
	        g2.setFont(new Font("Monospaced", Font.BOLD, 36));
	        g2.drawString("SISTEMA DE ARCHIVOS", 250, 80);
	        
	        
	        g2.setColor(Color.GRAY);
	        g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
	        g2.drawString("v1.0 - Estructuras de Datos", 350, 110);
	        
	        
	        g2.setColor(Color.WHITE);
	        g2.setFont(new Font("Monospaced", Font.PLAIN, 20));
	        
	        int y = 180;
	        for (String linea : menuPrincipal) {
	            g2.drawString(linea, 250, y);
	            y += 25;
	        }
	        
	        
	        
	        
	    }
	    
	    private void dibujarTexto(Graphics2D g2, String[] lineas, Color colorTexto) {
	        g2.setColor(colorTexto);
	        g2.setFont(new Font("Monospaced", Font.PLAIN, 16));
	        
	        int y = 50;
	        for (String linea : lineas) {
	            g2.drawString(linea, 250, y);
	            y += 22;
	        }
	    }
	    private void dibujarBarraEstado(Graphics2D g2) {
	        
	        g2.setColor(new Color(0, 40, 80, 200));
	        g2.fillRect(0, screenHeight - 40, screenWidth, 40);
	        
	        
	        g2.setColor(Color.WHITE);
	        g2.setFont(new Font("Monospaced", Font.PLAIN, 14));
	        
	        
	        
	        g2.setColor(Color.YELLOW);
	        String shortcuts = "[F1] Ayuda | [ESC] Salir | [F5] Actualizar";
	        g2.drawString(shortcuts, screenWidth - 400, screenHeight - 15);
	    }
	 
	 public void keyPressed(KeyEvent e) {
		 switch (e.getKeyCode()) {
         case KeyEvent.VK_F1:
             estadoActual = Estado.AYUDA;
             mensajeEstado = "Menú de ayuda activado";
             break;
             
         case KeyEvent.VK_ESCAPE:
             if (estadoActual == Estado.MENU_PRINCIPAL) {
            	 mensajeEstado = "Saliendo del programa";
            	 System.exit(0);
            	 break;
             }
             else {
            	 estadoActual=Estado.MENU_PRINCIPAL;
            	 mensajeEstado = "Volviendo al menú principal";
            	 break;
             }
             
             
         case KeyEvent.VK_1:
             if (estadoActual == Estado.MENU_PRINCIPAL) {
                 estadoActual = Estado.OPERACIONES;
                 mensajeEstado = "Modo operaciones activado";
             }
             break;
             
         case KeyEvent.VK_2:
             if (estadoActual == Estado.MENU_PRINCIPAL) {
                 estadoActual = Estado.INFO_ARBOL;
                 mensajeEstado = "Información del árbol";
             }
             break;
             
         case KeyEvent.VK_3:
             if (estadoActual == Estado.MENU_PRINCIPAL) {
                 estadoActual = Estado.AYUDA;
                 mensajeEstado = "Menú de ayuda";
             }
             break;
             
         case KeyEvent.VK_4:
             if (estadoActual == Estado.MENU_PRINCIPAL) {
                 estadoActual = Estado.OPERACIONES;
                 mensajeEstado = "Exportar/Importar datos";
             }
             break;
             
         case KeyEvent.VK_5:
             if (estadoActual == Estado.MENU_PRINCIPAL) {
                 
                 estadoActual = Estado.AYUDA;
                 mensajeEstado = "Información del proyecto";
             }
             break;
             
         case KeyEvent.VK_0:
             System.exit(0);
             break;
             
         case KeyEvent.VK_E:
             if (estadoActual == Estado.OPERACIONES) {
                 arbol.exportToJSON("export_" + System.currentTimeMillis() + ".json");
                 mensajeEstado = "Árbol exportado a JSON";
             }
             break;
             
         case KeyEvent.VK_I:
             if (estadoActual == Estado.OPERACIONES) {
                 arbol.importFromJSON("backup.json");
                 mensajeEstado = "Árbol importado desde JSON";
             }
             break;
             
         case KeyEvent.VK_F5:
             mensajeEstado = "Interfaz actualizada - " + new java.util.Date();
             break;
             
         case KeyEvent.VK_T:
             if (estadoActual == Estado.INFO_ARBOL) {
                 System.out.println("\n=== ÁRBOL COMPLETO ===");
                 arbol.displayTree();
                 mensajeEstado = "Árbol mostrado en consola";
             }
             break;
     }
     repaint();
 }
	    
	    
	    public void keyTyped(KeyEvent e) {}
	    public void keyReleased(KeyEvent e) {}

}
