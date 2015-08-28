class UpdatecaAERSDefaultHelpURL extends edu.northwestern.bioinformatics.bering.Migration {
	
    void up() {
      execute("update configuration set value = 'https://wiki.nci.nih.gov/display/caAERS/' where key = 'caaersBaseHelpUrl'");
    }

    void down() {
      
    }
}