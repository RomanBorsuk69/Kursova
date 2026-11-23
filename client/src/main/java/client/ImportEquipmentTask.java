package client;

import javafx.concurrent.Task;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.logging.Logger;

public class ImportEquipmentTask extends Task<Integer> {
    private static final Logger LOG=Logger.getLogger(ImportEquipmentTask.class.getName());
    private static final String HEADER="inventory_number;name;category;laboratory;criticality_level;responsible_specialist_id";
    private final Path file;
    private final HttpLabClient api;
    private volatile int skipped;
    public ImportEquipmentTask(Path file,HttpLabClient api) { this.file=file; this.api=api; }

    public int getSkipped() { return skipped; }

    @Override protected Integer call() throws Exception {
        LOG.info("Початок CSV-імпорту: "+file);
        List<String> lines=readLines();
        if(lines.isEmpty() || !HEADER.equals(lines.get(0).replace("﻿","").trim())) throw new IllegalArgumentException("Неправильний заголовок CSV");
        int total=Math.max(1,(int)lines.stream().skip(1).filter(l->!l.isBlank()).count()), imported=0, processed=0;
        for(int i=1;i<lines.size();i++) {
            if(lines.get(i).isBlank()) continue; // порожні рядки (зокрема в кінці файла) ігноруються
            try {
                String[] p=lines.get(i).split(";",-1);
                if(p.length!=6) throw new IllegalArgumentException("очікується 6 полів");
                Equipment e=new Equipment(null,p[0].trim(),p[1].trim(),p[2].trim(),p[3].trim(),
                        Integer.parseInt(p[4].trim()),Long.parseLong(p[5].trim()));
                api.addEquipment(e); imported++;
            } catch(NumberFormatException e) {
                skipped++;
                LOG.warning("Пропущено рядок "+(i+1)+": некоректне число у полях «criticality_level» або «responsible_specialist_id»");
            } catch(IllegalArgumentException e) {
                skipped++;
                LOG.warning("Пропущено рядок "+(i+1)+": "+e.getMessage());
            }
            processed++; updateProgress(processed,total); updateMessage("Імпортовано: "+imported+" із "+total);
        }
        LOG.info("CSV-імпорт завершено. Імпортовано: "+imported+", пропущено: "+skipped);
        return imported;
    }

    private List<String> readLines() throws IOException {
        if(!Files.isRegularFile(file)) throw new IOException("CSV-файл не знайдено: "+file);
        try { return Files.readAllLines(file,StandardCharsets.UTF_8); }
        catch(CharacterCodingException e) { throw new IOException("CSV-файл має бути в кодуванні UTF-8"); }
    }
}
