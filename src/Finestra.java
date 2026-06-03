import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.fazecast.jSerialComm.*;

public class Finestra extends JFrame {

    private BufferedReader reader;
    private BufferedWriter writer;
    private int c = 3;
    private SerialPort portaArduino;
    private OutputStream output;

    // Percorso dei file (modifica se necessario)
    private static final String PSW_FILE_PATH = "C:\\Users\\Utente\\Desktop\\InterfacciaGraficaProgetto\\psNuova.txt";
    private static final String LOG_FILE_PATH = "C:\\Users\\Utente\\Desktop\\InterfacciaGraficaProgetto\\accessLog.txt";

    // ╔══════════════════════════════════════════╗
    // ║          DESIGN SYSTEM — TOKENS          ║
    // ╚══════════════════════════════════════════╝
    private static final Color BG1          = new Color(6,   8,  20);
    private static final Color BG2          = new Color(18,  10, 42);
    private static final Color GLOW_CENTER  = new Color(70,  45, 140, 55);
    private static final Color GLASS_FILL   = new Color(255, 255, 255, 16);
    private static final Color GLASS_BORDER = new Color(255, 255, 255, 48);
    private static final Color ACCENT_A     = new Color(80,  165, 255);
    private static final Color ACCENT_B     = new Color(165, 95,  255);
    private static final Color TXT_PRIMARY  = Color.WHITE;
    private static final Color TXT_SECONDARY= new Color(155, 160, 180);
    private static final Color INPUT_BG     = new Color(20,  22,  40);
    private static final Font  FONT_HERO    = new Font("Helvetica Neue", Font.BOLD,  48);
    private static final Font  FONT_SUB     = new Font("Helvetica Neue", Font.PLAIN, 12);
    private static final Font  FONT_LABEL   = new Font("Helvetica Neue", Font.BOLD,  10);
    private static final Font  FONT_BODY    = new Font("Helvetica Neue", Font.PLAIN, 13);

    // ╔══════════════════════════════════════════╗
    // ║         LOCALIZATION SYSTEM              ║
    // ╚══════════════════════════════════════════╝
    private static final String LANG_IT = "🇮🇹 Italiano";
    private static final String LANG_EN = "🇬🇧 English";
    private static final String LANG_ES = "🇪🇸 Español";
    private static final String LANG_DE = "🇩🇪 Deutsch";
    private static final String LANG_ZH = "🇨🇳 中文";

    private static final Map<String, Map<String, String>> TRANSLATIONS = new HashMap<>();

    static {
        // ── Italiano ──
        Map<String, String> it = new HashMap<>();
        it.put("btn_log",          "📜   Storico Accessi");
        it.put("btn_clear",        "🗑️  Svuota Cronologia");
        it.put("msg_empty_log",    "Nessun tentativo di accesso registrato.");
        it.put("title",            "LOCK  OPENING");
        it.put("actions_label",    "A Z I O N I");
        it.put("btn_password",     "🔑   Cambia Password");
        it.put("btn_dica",         "✨   SG. DICA");
        it.put("info_label",       "I N F O");
        it.put("info_desc",        "<html><div style='width:100%;text-align:center;color:#9BA0B4;line-height:1.6;padding:0 5px;'>"
                + "Benvenuti nell'interfaccia di controllo della serratura digitale.<br><br>"
                + "Il sistema garantisce la sicurezza attraverso tre modalità: "
                + "<b style='color:#50A5FF'>password</b>, "
                + "<b style='color:#A55FFF'>impronta</b> e "
                + "<b style='color:#50A5FF'>NFC contactless</b>.<br><br>"
                + "Inserire le credenziali per procedere."
                + "</div></html>");
        it.put("card_title",       "🔒  Inserimento Password");
        it.put("card_sub",         "Inserisci le credenziali per accedere al sistema");
        it.put("pass_label",       "PASSWORD");
        it.put("btn_cancel",       "Annulla");
        it.put("btn_unlock",       "Sblocca  →");
        it.put("btn_luigi",        "👤   NON CLICCARE");
        it.put("dlg_curr_pass",    "Inserisci qui sotto la password corrente: ");
        it.put("dlg_curr_title",   "Password corrente");
        it.put("dlg_new_pass",     "Inserisci qui sotto la nuova password: ");
        it.put("dlg_new_title",    "Nuova password");
        it.put("dlg_wrong_pass",   "Password errata...");
        it.put("dlg_warning",      "Attenzione");
        it.put("dlg_success",      "Serratura Aperta!");
        it.put("dlg_succ_title",   "Successo...");
        it.put("dlg_denied",       "Accesso Negato!  Tentativi rimasti: ");
        it.put("dlg_err_title",    "Errore");
        it.put("dlg_alarm",        "Allarme scattato!");
        it.put("dlg_alarm_title",  "Attenzione...");
        it.put("dlg_luigi_title",  "Luigi Deluxe");
        it.put("dlg_dica_title",   "Il Signor Dica");
        it.put("toggle_open",      "☰  Azioni");
        it.put("toggle_close",     "✕  Chiudi");
        it.put("toggle_info_open", "ℹ  Info");
        it.put("toggle_info_close","✕  Info");
        TRANSLATIONS.put(LANG_IT, it);

        // ── English ──
        Map<String, String> en = new HashMap<>();
        en.put("btn_log",          "📜   Access Log");
        en.put("btn_clear",        "🗑️  Clear Log");
        en.put("msg_empty_log",    "No access attempts recorded.");
        en.put("title",            "LOCK  OPENING");
        en.put("actions_label",    "A C T I O N S");
        en.put("btn_password",     "🔑   Change Password");
        en.put("btn_dica",         "✨   MR. HINT");
        en.put("info_label",       "I N F O");
        en.put("info_desc",        "<html><div style='width:100%;text-align:center;color:#9BA0B4;line-height:1.6;padding:0 5px;'>"
                + "Welcome to the digital lock control interface.<br><br>"
                + "The system ensures security through three methods: "
                + "<b style='color:#50A5FF'>password</b>, "
                + "<b style='color:#A55FFF'>fingerprint</b> and "
                + "<b style='color:#50A5FF'>NFC contactless</b>.<br><br>"
                + "Enter your credentials to proceed."
                + "</div></html>");
        en.put("card_title",       "🔒  Password Entry");
        en.put("card_sub",         "Enter your credentials to access the system");
        en.put("pass_label",       "PASSWORD");
        en.put("btn_cancel",       "Cancel");
        en.put("btn_unlock",       "Unlock  →");
        en.put("btn_luigi",        "👤   LUIGI PIZZULLI");
        en.put("dlg_curr_pass",    "Enter the current password below: ");
        en.put("dlg_curr_title",   "Current password");
        en.put("dlg_new_pass",     "Enter the new password below: ");
        en.put("dlg_new_title",    "New password");
        en.put("dlg_wrong_pass",   "Wrong password...");
        en.put("dlg_warning",      "Warning");
        en.put("dlg_success",      "Lock Opened!");
        en.put("dlg_succ_title",   "Success...");
        en.put("dlg_denied",       "Access Denied!  Attempts remaining: ");
        en.put("dlg_err_title",    "Error");
        en.put("dlg_alarm",        "Alarm triggered!");
        en.put("dlg_alarm_title",  "Warning...");
        en.put("dlg_luigi_title",  "Luigi Deluxe");
        en.put("dlg_dica_title",   "Mr. Hint");
        en.put("toggle_open",      "☰  Actions");
        en.put("toggle_close",     "✕  Close");
        en.put("toggle_info_open", "ℹ  Info");
        en.put("toggle_info_close","✕  Info");
        TRANSLATIONS.put(LANG_EN, en);

        // ── Español ──
        Map<String, String> es = new HashMap<>();
        es.put("btn_log",          "📜   Historial de Acceso");
        es.put("btn_clear",        "🗑️  Vaciar Historial");
        es.put("msg_empty_log",    "No hay intentos de acceso registrados.");
        es.put("title",            "LOCK  OPENING");
        es.put("actions_label",    "A C C I O N E S");
        es.put("btn_password",     "🔑   Cambiar Contraseña");
        es.put("btn_dica",         "✨   SR. PISTA");
        es.put("info_label",       "I N F O");
        es.put("info_desc",        "<html><div style='width:100%;text-align:center;color:#9BA0B4;line-height:1.6;padding:0 5px;'>"
                + "Bienvenido a la interfaz de control de la cerradura digital.<br><br>"
                + "El sistema garantiza la seguridad mediante tres métodos: "
                + "<b style='color:#50A5FF'>contraseña</b>, "
                + "<b style='color:#A55FFF'>huella dactilar</b> y "
                + "<b style='color:#50A5FF'>NFC sin contacto</b>.<br><br>"
                + "Introduzca sus credenciales para continuar."
                + "</div></html>");
        es.put("card_title",       "🔒  Introducir Contraseña");
        es.put("card_sub",         "Introduce tus credenciales para acceder al sistema");
        es.put("pass_label",       "CONTRASEÑA");
        es.put("btn_cancel",       "Cancelar");
        es.put("btn_unlock",       "Desbloquear  →");
        es.put("btn_luigi",        "👤   LUIGI PIZZULLI");
        es.put("dlg_curr_pass",    "Introduce la contraseña actual: ");
        es.put("dlg_curr_title",   "Contraseña actual");
        es.put("dlg_new_pass",     "Introduce la nueva contraseña: ");
        es.put("dlg_new_title",    "Nueva contraseña");
        es.put("dlg_wrong_pass",   "Contraseña incorrecta...");
        es.put("dlg_warning",      "Atención");
        es.put("dlg_success",      "¡Cerradura abierta!");
        es.put("dlg_succ_title",   "Éxito...");
        es.put("dlg_denied",       "¡Acceso denegado!  Intentos restantes: ");
        es.put("dlg_err_title",    "Error");
        es.put("dlg_alarm",        "¡Alarma activada!");
        es.put("dlg_alarm_title",  "Atención...");
        es.put("dlg_luigi_title",  "Luigi Deluxe");
        es.put("dlg_dica_title",   "Sr. Pista");
        es.put("toggle_open",      "☰  Acciones");
        es.put("toggle_close",     "✕  Cerrar");
        es.put("toggle_info_open", "ℹ  Info");
        es.put("toggle_info_close","✕  Info");
        TRANSLATIONS.put(LANG_ES, es);

        // ── Deutsch ──
        Map<String, String> de = new HashMap<>();
        de.put("btn_log",          "📜   Zugriffsprotokoll");
        de.put("btn_clear",        "🗑️  Protokoll leeren");
        de.put("msg_empty_log",    "Keine Zugriffsversuche aufgezeichnet.");
        de.put("title",            "LOCK  OPENING");
        de.put("actions_label",    "A K T I O N E N");
        de.put("btn_password",     "🔑   Passwort ändern");
        de.put("btn_dica",         "✨   HR. HINWEIS");
        de.put("info_label",       "I N F O");
        de.put("info_desc",        "<html><div style='width:100%;text-align:center;color:#9BA0B4;line-height:1.6;padding:0 5px;'>"
                + "Willkommen in der Steuerungsoberfläche des digitalen Schlosses.<br><br>"
                + "Das System gewährleistet Sicherheit durch drei Methoden: "
                + "<b style='color:#50A5FF'>Passwort</b>, "
                + "<b style='color:#A55FFF'>Fingerabdruck</b> und "
                + "<b style='color:#50A5FF'>NFC kontaktlos</b>.<br><br>"
                + "Geben Sie Ihre Zugangsdaten ein, um fortzufahren."
                + "</div></html>");
        de.put("card_title",       "🔒  Passwort eingeben");
        de.put("card_sub",         "Geben Sie Ihre Zugangsdaten ein, um auf das System zuzugreifen");
        de.put("pass_label",       "PASSWORT");
        de.put("btn_cancel",       "Abbrechen");
        de.put("btn_unlock",       "Entsperren  →");
        de.put("btn_luigi",        "👤   LUIGI PIZZULLI");
        de.put("dlg_curr_pass",    "Aktuelles Passwort eingeben: ");
        de.put("dlg_curr_title",   "Aktuelles Passwort");
        de.put("dlg_new_pass",     "Neues Passwort eingeben: ");
        de.put("dlg_new_title",    "Neues Passwort");
        de.put("dlg_wrong_pass",   "Falsches Passwort...");
        de.put("dlg_warning",      "Achtung");
        de.put("dlg_success",      "Schloss geöffnet!");
        de.put("dlg_succ_title",   "Erfolg...");
        de.put("dlg_denied",       "Zugriff verweigert!  Versuche übrig: ");
        de.put("dlg_err_title",    "Fehler");
        de.put("dlg_alarm",        "Alarm ausgelöst!");
        de.put("dlg_alarm_title",  "Achtung...");
        de.put("dlg_luigi_title",  "Luigi Deluxe");
        de.put("dlg_dica_title",   "Hr. Hinweis");
        de.put("toggle_open",      "☰  Aktionen");
        de.put("toggle_close",     "✕  Schließen");
        de.put("toggle_info_open", "ℹ  Info");
        de.put("toggle_info_close","✕  Info");
        TRANSLATIONS.put(LANG_DE, de);

        // ── 中文 ──
        Map<String, String> zh = new HashMap<>();
        zh.put("btn_log",          "📜   访问日志");
        zh.put("btn_clear",        "🗑️  清除日志");
        zh.put("msg_empty_log",    "没有记录的访问尝试。");
        zh.put("title",            "LOCK  OPENING");
        zh.put("actions_label",    "操  作");
        zh.put("btn_password",     "🔑   更改密码");
        zh.put("btn_dica",         "✨   提示先生");
        zh.put("info_label",       "信  息");
        zh.put("info_desc",        "<html><div style='width:100%;text-align:center;color:#9BA0B4;line-height:1.6;padding:0 5px;'>"
                + "欢迎使用数字门锁控制界面。<br><br>"
                + "系统通过三种方式保障安全："
                + "<b style='color:#50A5FF'>密码</b>、"
                + "<b style='color:#A55FFF'>指纹</b>和"
                + "<b style='color:#50A5FF'>NFC 无感应</b>。<br><br>"
                + "请输入凭据以继续。"
                + "</div></html>");
        zh.put("card_title",       "🔒  输入密码");
        zh.put("card_sub",         "输入凭据以访问系统");
        zh.put("pass_label",       "密  码");
        zh.put("btn_cancel",       "取消");
        zh.put("btn_unlock",       "解锁  →");
        zh.put("btn_luigi",        "👤   LUIGI PIZZULLI");
        zh.put("dlg_curr_pass",    "请在下方输入当前密码：");
        zh.put("dlg_curr_title",   "当前密码");
        zh.put("dlg_new_pass",     "请在下方输入新密码：");
        zh.put("dlg_new_title",    "新密码");
        zh.put("dlg_wrong_pass",   "密码错误...");
        zh.put("dlg_warning",      "警告");
        zh.put("dlg_success",      "锁已打开！");
        zh.put("dlg_succ_title",   "成功...");
        zh.put("dlg_denied",       "拒绝访问！  剩余尝试次数：");
        zh.put("dlg_err_title",    "错误");
        zh.put("dlg_alarm",        "警报触发！");
        zh.put("dlg_alarm_title",  "警告...");
        zh.put("dlg_luigi_title",  "Luigi Deluxe");
        zh.put("dlg_dica_title",   "提示先生");
        zh.put("toggle_open",      "☰  操作");
        zh.put("toggle_close",     "✕  关闭");
        zh.put("toggle_info_open", "ℹ  信息");
        zh.put("toggle_info_close","✕  信息");
        TRANSLATIONS.put(LANG_ZH, zh);
    }

    // Current language (default: Italiano)
    private String currentLang = LANG_IT;

    private String t(String key) {
        return TRANSLATIONS.get(currentLang).getOrDefault(key, key);
    }

    // ╔══════════════════════════════════════════╗
    // ║     REFERENCES FOR LIVE LANG UPDATE      ║
    // ╚══════════════════════════════════════════╝

    // ── West panel collapsible state ──────────────────────
    private boolean westVisible = true;
    private Timer   slideTimerWest;
    private static final int WEST_W = 205;

    // ── East panel collapsible state ──────────────────────
    private boolean eastVisible = true;
    private Timer   slideTimerEast;
    private static final int EAST_W = 205;

    // Live-update references
    private JPanel  pWest;
    private JPanel  pEast;
    private JButton toggleBtn;          // toggle button per WEST (sinistra header)
    private JButton toggleEastBtn;      // toggle button per EAST (destra header)
    private JLabel  lActLabel;
    private JButton bPass, bLog, bDica;
    private DefaultListModel<String> accessLogModel = new DefaultListModel<>();
    private JLabel  lInfoLabel;
    private JLabel  lDesc;
    private JLabel  lockIcon;
    private JLabel  lSubCard;
    private JLabel  lPassLabel;
    private JButton bAnnulla, bSblocca, bLuigi;
    private JFrame  mainFrame;

    // ╔══════════════════════════════════════════╗
    // ║                CONSTRUCTOR               ║
    // ╚══════════════════════════════════════════╝
    public Finestra() {
        // Carica la cronologia salvata all'avvio
        caricaCronologia();

        /*
        portaArduino = SerialPort.getCommPort("COM3"); // Cambia con la tua porta (es. COM3 o /dev/ttyACM0)
        portaArduino.setComPortParameters(9600, 8, 1, 0);
        portaArduino.openPort();
        output = portaArduino.getOutputStream();

         */

        // ── Root background panel ──────────────────────────
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint base = new GradientPaint(0, 0, BG1, getWidth(), getHeight(), BG2);
                g2.setPaint(base);
                g2.fillRect(0, 0, getWidth(), getHeight());
                float cx = getWidth() / 2f, cy = getHeight() / 2f;
                float r  = Math.max(cx, cy) * 1.1f;
                RadialGradientPaint glow = new RadialGradientPaint(
                        new Point2D.Float(cx, cy), r,
                        new float[]{0f, 1f},
                        new Color[]{GLOW_CENTER, new Color(0, 0, 0, 0)}
                );
                g2.setPaint(glow);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bgPanel.setLayout(new BorderLayout(14, 14));
        bgPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ── JFrame setup ───────────────────────────────────
        mainFrame = new JFrame("Lock Opening");
        mainFrame.setSize(1200, 800);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(true);
        ImageIcon icon = new ImageIcon("C:\\Users\\Utente\\Desktop\\InterfacciaGraficaProgetto\\inconImg.png");
        mainFrame.setIconImage(icon.getImage());
        mainFrame.setContentPane(bgPanel);

        // ═══════════════════════════════════════════════════
        // NORTH — Header
        // ═══════════════════════════════════════════════════
        JPanel pNorth = makeGlassPanel(22);
        pNorth.setPreferredSize(new Dimension(0, 82));
        pNorth.setLayout(new BorderLayout());
        pNorth.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 24));

        // ── Toggle button WEST (sinistra) ──────────────────
        toggleBtn = makeGlassButton(t("toggle_close"), false);
        toggleBtn.setPreferredSize(new Dimension(130, 40));
        toggleBtn.setMaximumSize(new Dimension(130, 40));
        JPanel toggleWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toggleWrap.setOpaque(false);
        toggleWrap.setBorder(BorderFactory.createEmptyBorder(21, 8, 21, 0));
        toggleWrap.add(toggleBtn);
        pNorth.add(toggleWrap, BorderLayout.WEST);

        // ── Title (centre) ────────────────────────────────
        JLabel lTitle = new JLabel(t("title")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int w = fm.stringWidth(getText());
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_A, w, 0, ACCENT_B);
                g2.setPaint(gp);
                g2.drawString(getText(), 0, fm.getAscent());
                g2.dispose();
            }
            @Override
            public Dimension getPreferredSize() {
                FontMetrics fm = getFontMetrics(getFont());
                return new Dimension(fm.stringWidth(getText()) + 4, fm.getHeight() + 4);
            }
        };
        lTitle.setFont(FONT_HERO);
        lTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerStack = new JPanel();
        headerStack.setOpaque(false);
        headerStack.setLayout(new BoxLayout(headerStack, BoxLayout.Y_AXIS));
        headerStack.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        lTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerStack.add(lTitle);
        headerStack.add(Box.createVerticalStrut(3));
        pNorth.add(headerStack, BorderLayout.CENTER);

        // ── East controls: language selector + toggle EAST (destra) ──
        toggleEastBtn = makeGlassButton(t("toggle_info_close"), false);
        toggleEastBtn.setPreferredSize(new Dimension(130, 40));
        toggleEastBtn.setMaximumSize(new Dimension(130, 40));

        String[] langs = {LANG_IT, LANG_EN, LANG_ES, LANG_DE, LANG_ZH};
        JComboBox<String> langCombo = new JComboBox<>(langs);
        langCombo.setSelectedItem(LANG_IT);
        langCombo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        langCombo.setForeground(TXT_PRIMARY);
        langCombo.setBackground(INPUT_BG);
        langCombo.setFocusable(false);
        langCombo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        langCombo.setPreferredSize(new Dimension(150, 34));
        langCombo.setBorder(new RoundBorder(GLASS_BORDER, 8));
        langCombo.setOpaque(true);

        // Pannello destra dell'header: combo + toggle east
        JPanel eastHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        eastHeaderPanel.setOpaque(false);
        eastHeaderPanel.setBorder(BorderFactory.createEmptyBorder(21, 0, 21, 0));
        eastHeaderPanel.add(langCombo);
        eastHeaderPanel.add(toggleEastBtn);
        pNorth.add(eastHeaderPanel, BorderLayout.EAST);

        bgPanel.add(pNorth, BorderLayout.NORTH);

        // ═══════════════════════════════════════════════════
        // WEST — Action Sidebar (collapsible)
        // ═══════════════════════════════════════════════════
        pWest = makeGlassPanel(20);
        pWest.setPreferredSize(new Dimension(WEST_W, 0));
        pWest.setLayout(new BoxLayout(pWest, BoxLayout.Y_AXIS));
        pWest.setBorder(BorderFactory.createEmptyBorder(24, 14, 20, 14));

        lActLabel = makeLabel(t("actions_label"), FONT_LABEL, TXT_SECONDARY);
        lActLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pWest.add(lActLabel);
        pWest.add(Box.createVerticalStrut(20));
        pWest.add(makeSeparator());
        pWest.add(Box.createVerticalStrut(20));

        bPass = makeGlassButton(t("btn_password"), false);
        bPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        bLog = makeGlassButton(t("btn_log"), false);
        bLog.setAlignmentX(Component.CENTER_ALIGNMENT);


        // ── LOGICA: Cambia Password ────────────────────────
        bPass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String passFile = null;
                try {
                    reader = new BufferedReader(new FileReader(PSW_FILE_PATH));
                    passFile = reader.readLine();
                } catch (Exception ex) {}

                JPasswordField passCorrente = new JPasswordField(20);
                Object[] content1 = {t("dlg_curr_pass"), passCorrente};
                JOptionPane.showMessageDialog(mainFrame, content1, t("dlg_curr_title"),
                        JOptionPane.INFORMATION_MESSAGE);
                String passCorrenteTxt = new String(passCorrente.getPassword());

                if (passCorrenteTxt.equals(passFile)) {
                    JPasswordField nuovaPass = new JPasswordField(20);
                    Object[] content2 = {t("dlg_new_pass"), nuovaPass};
                    JOptionPane.showMessageDialog(mainFrame, content2, t("dlg_new_title"),
                            JOptionPane.INFORMATION_MESSAGE);
                    String pass = new String(nuovaPass.getPassword());
                    try {
                        writer = new BufferedWriter(new FileWriter(PSW_FILE_PATH));
                        writer.write(pass);
                        writer.close();
                    } catch (Exception e1) {
                        System.out.println("Errore nel salvare la password...");
                    }
                } else {
                    JOptionPane.showMessageDialog(mainFrame, t("dlg_wrong_pass"), t("dlg_warning"),
                            JOptionPane.ERROR_MESSAGE);
                }
                try {
                    if (reader != null) reader.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // ── LOGICA: Storico Accessi ───────────────────
        bLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostraCronologiaAccessi();
            }
        });

        pWest.add(bPass);
        pWest.add(Box.createVerticalStrut(10));
        pWest.add(bLog);
        pWest.add(Box.createVerticalGlue());
        pWest.add(makeSeparator());
        pWest.add(Box.createVerticalStrut(14));

        bDica = makeGlassButton(t("btn_dica"), true);
        bDica.setAlignmentX(Component.CENTER_ALIGNMENT);
        bDica.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon dicaIMG = new ImageIcon(
                        "C:\\Users\\Utente\\Desktop\\InterfacciaGraficaProgetto\\sgDica.png");
                JLabel sgDica = new JLabel(dicaIMG);
                sgDica.setPreferredSize(new Dimension(420, 420));
                JOptionPane.showMessageDialog(mainFrame, new Object[]{sgDica},
                        t("dlg_dica_title"), JOptionPane.WARNING_MESSAGE);
            }
        });
        pWest.add(bDica);
        bgPanel.add(pWest, BorderLayout.WEST);

        // ═══════════════════════════════════════════════════
        // EAST — Info Sidebar (collapsible)
        // ═══════════════════════════════════════════════════
        pEast = makeGlassPanel(20);
        pEast.setPreferredSize(new Dimension(EAST_W, 0));
        pEast.setLayout(new BorderLayout());
        pEast.setBorder(BorderFactory.createEmptyBorder(24, 14, 20, 14));

        JPanel eastStack = new JPanel();
        eastStack.setOpaque(false);
        eastStack.setLayout(new BoxLayout(eastStack, BoxLayout.Y_AXIS));

        lInfoLabel = makeLabel(t("info_label"), FONT_LABEL, TXT_SECONDARY);
        lInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lTeamName = makeLabel("\"Lock Opening\"",
                new Font("Helvetica Neue", Font.ITALIC, 13), ACCENT_A);
        lTeamName.setAlignmentX(Component.CENTER_ALIGNMENT);

        lDesc = new JLabel(t("info_desc"));
        lDesc.setFont(FONT_BODY);
        lDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        eastStack.add(lInfoLabel);
        eastStack.add(Box.createVerticalStrut(8));
        eastStack.add(lTeamName);
        eastStack.add(Box.createVerticalStrut(16));
        eastStack.add(makeSeparator());
        eastStack.add(Box.createVerticalStrut(16));
        eastStack.add(lDesc);

        pEast.add(eastStack, BorderLayout.CENTER);

        bLuigi = makeGlassButton(t("btn_luigi"), true);
        bLuigi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon luigiIMG = new ImageIcon(
                        "C:\\Users\\Utente\\Desktop\\InterfacciaGraficaProgetto\\luigi2.jpeg");
                JLabel luigi = new JLabel(luigiIMG);
                luigi.setPreferredSize(new Dimension(600, 600));
                JOptionPane.showMessageDialog(mainFrame, new Object[]{luigi},
                        t("dlg_luigi_title"), JOptionPane.WARNING_MESSAGE);
            }
        });
        pEast.add(bLuigi, BorderLayout.SOUTH);
        bgPanel.add(pEast, BorderLayout.EAST);

        // ═══════════════════════════════════════════════════
        // CENTER — Password Card
        // ═══════════════════════════════════════════════════
        JPanel pCenter = makeGlassPanel(24);
        pCenter.setLayout(new GridBagLayout());

        JPanel card = makeGlassPanel(20);
        card.setPreferredSize(new Dimension(380, 298));
        card.setLayout(new BorderLayout());

        // Card top gradient band
        JPanel cardBand = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_A, getWidth(), 0, ACCENT_B);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() + 20, 20, 20));
                g2.dispose();
            }
        };
        cardBand.setOpaque(false);
        cardBand.setPreferredSize(new Dimension(0, 46));
        cardBand.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 12));
        lockIcon = makeLabel(t("card_title"),
                new Font("Helvetica Neue", Font.BOLD, 14), TXT_PRIMARY);
        cardBand.add(lockIcon);
        card.add(cardBand, BorderLayout.NORTH);

        // Card body
        JPanel cardBody = new JPanel();
        cardBody.setOpaque(false);
        cardBody.setLayout(new BoxLayout(cardBody, BoxLayout.Y_AXIS));
        cardBody.setBorder(BorderFactory.createEmptyBorder(20, 26, 10, 26));

        lSubCard = makeLabel(t("card_sub"), FONT_SUB, TXT_SECONDARY);
        lSubCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardBody.add(lSubCard);
        cardBody.add(Box.createVerticalStrut(20));

        lPassLabel = makeLabel(t("pass_label"), FONT_LABEL, TXT_SECONDARY);
        lPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardBody.add(lPassLabel);
        cardBody.add(Box.createVerticalStrut(7));

        JPasswordField txtPassword = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(INPUT_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtPassword.setFont(new Font("Helvetica Neue", Font.PLAIN, 18));
        txtPassword.setForeground(TXT_PRIMARY);
        txtPassword.setCaretColor(ACCENT_A);
        txtPassword.setOpaque(false);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(GLASS_BORDER, 12),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        txtPassword.setMaximumSize(new Dimension(300, 48));
        txtPassword.setPreferredSize(new Dimension(300, 48));
        txtPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardBody.add(txtPassword);
        card.add(cardBody, BorderLayout.CENTER);

        // Card buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 14));
        btnRow.setOpaque(false);

        bAnnulla = makeGlassButton(t("btn_cancel"), false);
        bAnnulla.setPreferredSize(new Dimension(120, 40));

        bSblocca = makePrimaryButton(t("btn_unlock"));
        bSblocca.setPreferredSize(new Dimension(148, 40));

        // ── LOGICA: Annulla ────────────────────────────────
        bAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPassword.setText("");
            }
        });

        // ── LOGICA: Sblocca ────────────────────────────────
        bSblocca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String psw = null;
                try {
                    reader = new BufferedReader(new FileReader(PSW_FILE_PATH));
                    psw = reader.readLine();
                    reader.close();
                } catch (Exception ex) {}

                String password = new String(txtPassword.getPassword());
                // Creiamo il formato per la data e l'ora
                String timeStamp = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(new Date());

                if (password.equals(psw)) {
                    // Inseriamo in cima alla lista (indice 0) per avere il più recente in alto
                    accessLogModel.add(0, "✅ Successo  |  " + timeStamp);
                    salvaCronologia(); // Salviamo sul file
                    JOptionPane.showMessageDialog(mainFrame, t("dlg_success"), t("dlg_succ_title"),
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    accessLogModel.add(0, "❌ Fallimento |  " + timeStamp);
                    salvaCronologia(); // Salviamo sul file
                    JOptionPane.showMessageDialog(mainFrame,
                            t("dlg_denied") + c,
                            t("dlg_err_title"), JOptionPane.ERROR_MESSAGE);
                    c = c - 1;
                    if (c < 0) {
                        JOptionPane.showMessageDialog(mainFrame, t("dlg_alarm"),
                                t("dlg_alarm_title"), JOptionPane.WARNING_MESSAGE);
                        bSblocca.setEnabled(false);
                    }
                }
            } // <-- Parentesi rimessa a posto!
        });

        btnRow.add(bAnnulla);
        btnRow.add(bSblocca);
        card.add(btnRow, BorderLayout.SOUTH);

        pCenter.add(card);
        bgPanel.add(pCenter, BorderLayout.CENTER);

        // ═══════════════════════════════════════════════════
        // TOGGLE WEST — Collapsible West Panel (eased animation)
        // ═══════════════════════════════════════════════════
        toggleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (slideTimerWest != null && slideTimerWest.isRunning()) slideTimerWest.stop();

                westVisible = !westVisible;
                final int startW   = pWest.getPreferredSize().width;
                final int endW     = westVisible ? WEST_W : 0;
                final int DURATION = 300;
                final int INTERVAL = 8;
                final long startTime = System.currentTimeMillis();

                toggleBtn.setText(westVisible ? t("toggle_close") : t("toggle_open"));
                toggleBtn.setEnabled(false);
                if (westVisible) pWest.setVisible(true);

                slideTimerWest = new Timer(INTERVAL, null);
                slideTimerWest.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        float raw  = (float)(System.currentTimeMillis() - startTime) / DURATION;
                        float tVal = Math.min(raw, 1f);
                        float ease = tVal < 0.5f
                                ? 4 * tVal * tVal * tVal
                                : 1 - (float)Math.pow(-2 * tVal + 2, 3) / 2;

                        int current = Math.round(startW + (endW - startW) * ease);
                        pWest.setPreferredSize(new Dimension(current, 0));
                        bgPanel.revalidate();

                        if (tVal >= 1f) {
                            slideTimerWest.stop();
                            pWest.setPreferredSize(new Dimension(endW, 0));
                            pWest.setVisible(westVisible);
                            toggleBtn.setEnabled(true);
                            bgPanel.revalidate();
                        }
                    }
                });
                slideTimerWest.start();
            }
        });

        // ═══════════════════════════════════════════════════
        // TOGGLE EAST — Collapsible East Panel (eased animation)
        // ═══════════════════════════════════════════════════
        toggleEastBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (slideTimerEast != null && slideTimerEast.isRunning()) slideTimerEast.stop();

                eastVisible = !eastVisible;
                final int startW   = pEast.getPreferredSize().width;
                final int endW     = eastVisible ? EAST_W : 0;
                final int DURATION = 300;
                final int INTERVAL = 8;
                final long startTime = System.currentTimeMillis();

                toggleEastBtn.setText(eastVisible ? t("toggle_info_close") : t("toggle_info_open"));
                toggleEastBtn.setEnabled(false);
                if (eastVisible) pEast.setVisible(true);

                slideTimerEast = new Timer(INTERVAL, null);
                slideTimerEast.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        float raw  = (float)(System.currentTimeMillis() - startTime) / DURATION;
                        float tVal = Math.min(raw, 1f);
                        // Cubic ease-in-out identico al pannello WEST
                        float ease = tVal < 0.5f
                                ? 4 * tVal * tVal * tVal
                                : 1 - (float)Math.pow(-2 * tVal + 2, 3) / 2;

                        int current = Math.round(startW + (endW - startW) * ease);
                        pEast.setPreferredSize(new Dimension(current, 0));
                        bgPanel.revalidate();

                        if (tVal >= 1f) {
                            slideTimerEast.stop();
                            pEast.setPreferredSize(new Dimension(endW, 0));
                            pEast.setVisible(eastVisible);
                            toggleEastBtn.setEnabled(true);
                            bgPanel.revalidate();
                        }
                    }
                });
                slideTimerEast.start();
            }
        });

        // ═══════════════════════════════════════════════════
        // LANGUAGE CHANGE — update all labels live
        // ═══════════════════════════════════════════════════
        langCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentLang = (String) langCombo.getSelectedItem();
                applyLanguage();
            }
        });

        mainFrame.setVisible(true);
    }

    // ╔══════════════════════════════════════════╗
    // ║         GESTIONE FILE CRONOLOGIA         ║
    // ╚══════════════════════════════════════════╝

    private void caricaCronologia() {
        File f = new File(LOG_FILE_PATH);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                accessLogModel.addElement(line);
            }
        } catch (IOException e) {
            System.out.println("Nessun file log trovato o errore di lettura.");
        }
    }

    private void salvaCronologia() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE_PATH))) {
            for (int i = 0; i < accessLogModel.getSize(); i++) {
                bw.write(accessLogModel.getElementAt(i));
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Errore nel salvare la cronologia.");
        }
    }

    private void mostraCronologiaAccessi() {
        JList<String> list = new JList<>(accessLogModel);
        list.setFont(new Font("Helvetica Neue", Font.PLAIN, 15));
        list.setBackground(INPUT_BG);
        list.setForeground(TXT_PRIMARY);
        // Disabilita la selezione degli elementi
        list.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                super.setSelectionInterval(-1, -1);
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(380, 250));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setBackground(INPUT_BG);

        // Inseriamo la lista in un pannello "glass" per mantenere la coerenza del design
        JPanel panel = makeGlassPanel(15);
        panel.setLayout(new BorderLayout(0, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottone per svuotare la lista
        JButton bClearLog = makeGlassButton(t("btn_clear"), true);
        bClearLog.setPreferredSize(new Dimension(200, 35));
        bClearLog.addActionListener(e -> {
            accessLogModel.clear(); // Svuota la UI in tempo reale
            salvaCronologia();      // Aggiorna e svuota il file di testo
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(bClearLog);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        if (accessLogModel.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, t("msg_empty_log"),
                    t("btn_log"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(mainFrame, panel, t("btn_log"), JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * Updates all live-referenced labels and buttons with the current language.
     */
    private void applyLanguage() {
        lActLabel.setText(t("actions_label"));
        bPass.setText(t("btn_password"));
        bLog.setText(t("btn_log"));
        bDica.setText(t("btn_dica"));
        lInfoLabel.setText(t("info_label"));
        lDesc.setText(t("info_desc"));
        lockIcon.setText(t("card_title"));
        lSubCard.setText(t("card_sub"));
        lPassLabel.setText(t("pass_label"));
        bAnnulla.setText(t("btn_cancel"));
        bSblocca.setText(t("btn_unlock"));
        bLuigi.setText(t("btn_luigi"));
        // Aggiorna entrambi i toggle con lo stato corrente di visibilità
        toggleBtn.setText(westVisible ? t("toggle_close") : t("toggle_open"));
        toggleEastBtn.setText(eastVisible ? t("toggle_info_close") : t("toggle_info_open"));

        pWest.revalidate();
        pWest.repaint();
        pEast.revalidate();
        pEast.repaint();
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    // ╔══════════════════════════════════════════╗
    // ║            UI COMPONENT HELPERS            ║
    // ╚══════════════════════════════════════════╝

    static JPanel makeGlassPanel(int arc) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GLASS_FILL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
                g2.setColor(GLASS_BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, arc, arc));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    static JLabel makeLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    static JSeparator makeSeparator() {
        JSeparator s = new JSeparator() {
            @Override
            protected void paintComponent(Graphics g) {
                if (getWidth() == 0) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                LinearGradientPaint lgp = new LinearGradientPaint(
                        0, 0, getWidth(), 0,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{
                                new Color(255, 255, 255, 0),
                                new Color(255, 255, 255, 40),
                                new Color(255, 255, 255, 0)
                        }
                );
                g2.setPaint(lgp);
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    static JButton makeGlassButton(String text, boolean accent) {
        JButton btn = new JButton(text) {
            private float hoverAlpha = 0f;
            private boolean pressed  = false;
            private Timer hoverTimer;

            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { animateHover(true);  }
                    @Override public void mouseExited (MouseEvent e) { animateHover(false); }
                    @Override public void mousePressed (MouseEvent e){ pressed = true;  repaint(); }
                    @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
                });
            }

            private void animateHover(boolean enter) {
                if (hoverTimer != null && hoverTimer.isRunning()) hoverTimer.stop();
                final float target = enter ? 1f : 0f;
                final float step   = enter ? 0.09f : -0.09f;
                hoverTimer = new Timer(10, null);
                hoverTimer.addActionListener(ae -> {
                    hoverAlpha = Math.max(0f, Math.min(1f, hoverAlpha + step));
                    repaint();
                    if ((enter && hoverAlpha >= 1f) || (!enter && hoverAlpha <= 0f)) hoverTimer.stop();
                });
                hoverTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int offY = pressed ? 2 : 0;
                g2.translate(0, offY);
                int fillAlpha, fillR, fillG, fillB;
                if (accent) {
                    fillAlpha = Math.round(25 + hoverAlpha * 45);
                    fillR = 80; fillG = 165; fillB = 255;
                } else {
                    fillAlpha = Math.round(16 + hoverAlpha * 28);
                    fillR = 255; fillG = 255; fillB = 255;
                }
                g2.setColor(new Color(fillR, fillG, fillB, fillAlpha));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() - offY, 14, 14));
                int shimmerAlpha = Math.round(20 + hoverAlpha * 35);
                LinearGradientPaint shimmer = new LinearGradientPaint(
                        0, 0, 0, getHeight() / 2f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(255, 255, 255, shimmerAlpha),
                                new Color(255, 255, 255, 0)}
                );
                g2.setPaint(shimmer);
                g2.fill(new RoundRectangle2D.Float(1, 1, getWidth() - 2, (getHeight() - offY) / 2f, 13, 13));
                int borderAlpha = accent
                        ? Math.round(90  + hoverAlpha * 80)
                        : Math.round(48  + hoverAlpha * 80);
                Color borderColor = accent
                        ? new Color(80, 165, 255, borderAlpha)
                        : new Color(255, 255, 255, borderAlpha);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(0.9f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - offY - 1, 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY);
        btn.setForeground(accent ? ACCENT_A : TXT_PRIMARY);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(172, 40));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }

    static JButton makePrimaryButton(String text) {
        JButton btn = new JButton(text) {
            private float hoverAlpha = 0f;
            private boolean pressed  = false;
            private Timer hoverTimer;

            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { animateHover(true);  }
                    @Override public void mouseExited (MouseEvent e) { animateHover(false); }
                    @Override public void mousePressed (MouseEvent e){ pressed = true;  repaint(); }
                    @Override public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
                });
            }

            private void animateHover(boolean enter) {
                if (hoverTimer != null && hoverTimer.isRunning()) hoverTimer.stop();
                final float step = enter ? 0.09f : -0.09f;
                hoverTimer = new Timer(10, null);
                hoverTimer.addActionListener(ae -> {
                    hoverAlpha = Math.max(0f, Math.min(1f, hoverAlpha + step));
                    repaint();
                    if ((enter && hoverAlpha >= 1f) || (!enter && hoverAlpha <= 0f)) hoverTimer.stop();
                });
                hoverTimer.start();
            }

            private int lerp(int a, int b, float t) { return Math.round(a + (b - a) * t); }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int offY = pressed ? 2 : 0;
                g2.translate(0, offY);
                if (hoverAlpha > 0.01f) {
                    int glowAlpha = Math.round(hoverAlpha * 60);
                    g2.setColor(new Color(80, 165, 255, glowAlpha));
                    for (int i = 4; i >= 1; i--) {
                        g2.setStroke(new BasicStroke(i * 2f));
                        g2.draw(new RoundRectangle2D.Float(-i, -i,
                                getWidth() + i * 2, getHeight() - offY + i * 2, 18, 18));
                    }
                }
                int rA = lerp(80,  110, hoverAlpha), gA = lerp(165, 185, hoverAlpha), bA = lerp(255, 255, hoverAlpha);
                int rB = lerp(165, 185, hoverAlpha), gB = lerp(95,  115, hoverAlpha), bB = lerp(255, 255, hoverAlpha);
                GradientPaint gp = new GradientPaint(0, 0, new Color(rA, gA, bA),
                        getWidth(), 0, new Color(rB, gB, bB));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight() - offY, 14, 14));
                LinearGradientPaint shimmer = new LinearGradientPaint(
                        0, 0, 0, (getHeight() - offY) / 2f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(255, 255, 255, 55),
                                new Color(255, 255, 255, 0)}
                );
                g2.setPaint(shimmer);
                g2.fill(new RoundRectangle2D.Float(1, 1, getWidth() - 2,
                        (getHeight() - offY) / 2f, 13, 13));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
        btn.setForeground(TXT_PRIMARY);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int   radius;

        RoundBorder(Color c, int r) {
            color  = c;
            radius = r;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f,
                    w - 1, h - 1, radius * 2, radius * 2));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(Finestra::new);
    }
}