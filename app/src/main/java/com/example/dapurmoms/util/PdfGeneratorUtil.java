package com.example.dapurmoms.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.ParcelFileDescriptor;

import com.example.dapurmoms.data.database.entity.BelanjaBahan;
import com.example.dapurmoms.data.database.entity.BiayaLain;
import com.example.dapurmoms.data.database.entity.Pesanan;
import com.example.dapurmoms.data.database.entity.PesananItem;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PdfGeneratorUtil {

    public static boolean generateReceiptPdf(Context context, ParcelFileDescriptor pfd, Pesanan pesanan) {
        PdfDocument pdfDocument = new PdfDocument();
        
        // Calculate dynamic height based on the number of items
        List<PesananItem> items = pesanan.getNamaMenu();
        int itemHeight = 26;
        int headerHeight = 210;
        int footerHeight = 100;
        int contentHeight = headerHeight + (items != null ? items.size() * itemHeight : 0) + footerHeight;
        if (contentHeight < 320) {
            contentHeight = 320;
        }

        // Native 58mm printer document size (180 width) to prevent driver downscaling
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(180, contentHeight, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        
        // Background
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        
        // Header Logo (Centered)
        try {
            android.graphics.Bitmap logo = android.graphics.BitmapFactory.decodeResource(context.getResources(), com.example.dapurmoms.R.drawable.logo_bulat);
            if (logo != null) {
                android.graphics.Bitmap scaledLogo = android.graphics.Bitmap.createScaledBitmap(logo, 180, 180, true);
                android.graphics.Rect destRect = new android.graphics.Rect(55, 8, 125, 78);
                canvas.drawBitmap(scaledLogo, null, destRect, paint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        paint.setColor(Color.BLACK);
        paint.setTextSize(11);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("DAPUR MOMS HIJRA", 90, 93, paint);
        
        paint.setTextSize(7.5f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Aneka Masakan & Kue", 90, 103, paint);
        canvas.drawText("Telp: 0822 8889 7288", 90, 112, paint);
        
        // Divider
        paint.setStrokeWidth(1.2f);
        canvas.drawLine(8, 120, 172, 120, paint);
        
        // Content
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(7.5f);
        
        int y = 133;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
        String dateStr = sdf.format(new Date(pesanan.getTanggal()));
        
        canvas.drawText("Tanggal : " + dateStr, 8, y, paint);
        y += 11;
        
        String namaPemesan = pesanan.getNamaPemesan().equals("-") ? "Pelanggan" : pesanan.getNamaPemesan();
        canvas.drawText("Pemesan : " + namaPemesan, 8, y, paint);
        y += 16;
        
        // Items Header
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Pesanan", 8, y, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Total", 172, y, paint);
        
        y += 5;
        paint.setStrokeWidth(0.8f);
        canvas.drawLine(8, y, 172, y, paint);
        y += 12;
        
        // Items List
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        if (items != null) {
            for (PesananItem item : items) {
                paint.setColor(Color.BLACK);
                paint.setTextSize(7.5f);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(item.getNamaMenu(), 8, y, paint);
                
                y += 9;
                paint.setColor(Color.DKGRAY);
                paint.setTextSize(6.5f);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                canvas.drawText(item.getJumlah() + " x " + CurrencyFormatter.formatRupiah(item.getHargaSatuan()), 8, y, paint);
                
                paint.setColor(Color.BLACK);
                paint.setTextSize(7.5f);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(CurrencyFormatter.formatRupiah(item.getTotal()), 172, y, paint);
                
                y += 13;
            }
        }
        
        y -= 3;
        paint.setStrokeWidth(1.2f);
        canvas.drawLine(8, y, 172, y, paint);
        y += 13;
        
        // Total Footer
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(8.5f);
        canvas.drawText("TOTAL", 8, y, paint);
        
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(9.5f);
        canvas.drawText(CurrencyFormatter.formatRupiah(pesanan.getTotal()), 172, y, paint);
        
        y += 12;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(7f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Metode Pembayaran", 8, y, paint);
        
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(pesanan.getMetodePembayaran(), 172, y, paint);
        
        y += 18;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint.setTextSize(7f);
        canvas.drawText("Terima Kasih atas Pesanan Anda!", 90, y, paint);
        
        pdfDocument.finishPage(page);
        
        try {
            FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
            pdfDocument.writeTo(fos);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            pdfDocument.close();
        }
    }

    public static boolean generateLaporanPdf(Context context, ParcelFileDescriptor pfd, String monthStr, 
                                             long pendapatan, long biayaBahan, long biayaOps, long hpp, long untung, double margin,
                                             List<Pesanan> pesananList, List<BelanjaBahan> belanjaList, List<BiayaLain> biayaList) {
        PdfDocument pdfDocument = new PdfDocument();
        int pageNum = 1;
        // A4 Size: 595 x 842 points
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNum).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        
        // Background
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        
        // Header
        try {
            android.graphics.Bitmap logo = android.graphics.BitmapFactory.decodeResource(context.getResources(), com.example.dapurmoms.R.drawable.logo_bulat);
            if (logo != null) {
                android.graphics.Bitmap scaledLogo = android.graphics.Bitmap.createScaledBitmap(logo, 500, 500, true);
                android.graphics.Rect destRect = new android.graphics.Rect(217, 30, 377, 190);
                canvas.drawBitmap(scaledLogo, null, destRect, paint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        paint.setColor(Color.BLACK);
        paint.setTextSize(28);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("DAPUR MOMS HIJRA", 297, 220, paint);
        
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Laporan Keuangan Bulanan", 297, 250, paint);
        canvas.drawText("Periode: " + monthStr, 297, 275, paint);
        
        // Divider
        paint.setStrokeWidth(2);
        canvas.drawLine(50, 300, 545, 300, paint);
        
        // Calculate breakdowns
        long pesananCash = 0, pesananTransfer = 0, pesananPiutang = 0;
        if (pesananList != null) {
            for (Pesanan p : pesananList) {
                if ("Transfer".equals(p.getMetodePembayaran())) pesananTransfer += p.getTotal();
                else if ("Piutang".equals(p.getMetodePembayaran())) pesananPiutang += p.getTotal();
                else pesananCash += p.getTotal();
            }
        }
        
        long belanjaCash = 0, belanjaTransfer = 0, belanjaUtang = 0;
        if (belanjaList != null) {
            for (BelanjaBahan b : belanjaList) {
                if ("Transfer".equals(b.getMetodePembayaran())) belanjaTransfer += b.getTotalHarga();
                else if ("Utang".equals(b.getMetodePembayaran())) belanjaUtang += b.getTotalHarga();
                else belanjaCash += b.getTotalHarga();
            }
        }
        
        long biayaCash = 0, biayaTransfer = 0, biayaUtang = 0;
        if (biayaList != null) {
            for (BiayaLain b : biayaList) {
                if ("Transfer".equals(b.getMetodePembayaran())) biayaTransfer += b.getJumlah();
                else if ("Utang".equals(b.getMetodePembayaran())) biayaUtang += b.getJumlah();
                else biayaCash += b.getJumlah();
            }
        }

        int y = 330;
        int leftX = 80;
        int rightX = 515;
        
        // Draw Row helper
        drawRow(canvas, paint, "Total Pendapatan (Kas)", CurrencyFormatter.formatRupiah(pendapatan), leftX, rightX, y, true);
        y += 18;
        drawRow(canvas, paint, "  • Cash", CurrencyFormatter.formatRupiah(pesananCash), leftX, rightX, y, false);
        y += 18;
        drawRow(canvas, paint, "  • Transfer", CurrencyFormatter.formatRupiah(pesananTransfer), leftX, rightX, y, false);
        y += 18;
        
        paint.setStrokeWidth(1);
        canvas.drawLine(leftX, y, rightX, y, paint);
        y += 22;
        
        drawRow(canvas, paint, "Pengeluaran Bahan Baku (Kas)", "- " + CurrencyFormatter.formatRupiah(biayaBahan), leftX, rightX, y, true);
        y += 18;
        drawRow(canvas, paint, "  • Cash", "- " + CurrencyFormatter.formatRupiah(belanjaCash), leftX, rightX, y, false);
        y += 18;
        drawRow(canvas, paint, "  • Transfer", "- " + CurrencyFormatter.formatRupiah(belanjaTransfer), leftX, rightX, y, false);
        y += 18;
        
        drawRow(canvas, paint, "Pengeluaran Operasional (Kas)", "- " + CurrencyFormatter.formatRupiah(biayaOps), leftX, rightX, y, true);
        y += 18;
        drawRow(canvas, paint, "  • Cash", "- " + CurrencyFormatter.formatRupiah(biayaCash), leftX, rightX, y, false);
        y += 18;
        drawRow(canvas, paint, "  • Transfer", "- " + CurrencyFormatter.formatRupiah(biayaTransfer), leftX, rightX, y, false);
        y += 18;
        
        canvas.drawLine(leftX, y, rightX, y, paint);
        y += 25;
        
        drawRow(canvas, paint, "Total Harga Pokok Penjualan (HPP Kas)", CurrencyFormatter.formatRupiah(hpp), leftX, rightX, y, true);
        y += 25;
        
        canvas.drawLine(leftX, y, rightX, y, paint);
        y += 30;
        
        // Laba Bersih
        paint.setTextSize(18);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.LEFT);
        String labelUntung = (untung >= 0) ? "Laba Bersih Kas" : "Rugi Bersih Kas";
        canvas.drawText(labelUntung, leftX, y, paint);
        
        paint.setTextAlign(Paint.Align.RIGHT);
        if (untung < 0) paint.setColor(Color.RED);
        else paint.setColor(Color.parseColor("#388E3C")); // Green
        canvas.drawText(CurrencyFormatter.formatRupiah(untung), rightX, y, paint);
        
        y += 25;
        paint.setColor(Color.BLACK);
        paint.setTextSize(14);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Margin Keuntungan", leftX, y, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format(new Locale("id", "ID"), "%.1f%%", margin), rightX, y, paint);

        y += 30;
        paint.setStrokeWidth(1);
        canvas.drawLine(leftX, y, rightX, y, paint);
        y += 25;

        // Outstanding Section
        paint.setTextSize(14);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("UTANG & PIUTANG USAHA (Belum Lunas)", leftX, y, paint);
        
        y += 18;
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        drawRow(canvas, paint, "  • Piutang Usaha (Uang Belum Masuk)", CurrencyFormatter.formatRupiah(pesananPiutang), leftX, rightX, y, false);
        y += 18;
        drawRow(canvas, paint, "  • Utang Usaha (Uang Belum Keluar)", CurrencyFormatter.formatRupiah(belanjaUtang + biayaUtang), leftX, rightX, y, false);
        
        y += 60;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint.setTextSize(10);
        canvas.drawText("Laporan dibuat secara otomatis oleh Aplikasi Catatan Keuangan Dapur Moms", 297, 810, paint);
        
        pdfDocument.finishPage(page);
        
        SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM", new Locale("id", "ID"));

        // Page: Rincian Pesanan
        if (pesananList != null && !pesananList.isEmpty()) {
            pageNum++;
            pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNum).create();
            page = pdfDocument.startPage(pageInfo);
            canvas = page.getCanvas();
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);
            
            paint.setColor(Color.BLACK);
            paint.setTextSize(20);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Rincian Pendapatan (Pesanan)", 50, 60, paint);
            
            paint.setStrokeWidth(2);
            canvas.drawLine(50, 75, 545, 75, paint);
            
            y = 100;
            for (Pesanan p : pesananList) {
                if (y > 780) {
                    pdfDocument.finishPage(page);
                    pageNum++;
                    pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNum).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    paint.setColor(Color.WHITE);
                    canvas.drawPaint(paint);
                    y = 60;
                }
                
                String date = dateFmt.format(new Date(p.getTanggal()));
                paint.setColor(Color.BLACK);
                paint.setTextSize(14);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(date + " - " + p.getNamaPemesan() + " [" + p.getMetodePembayaran() + "]", 50, y, paint);
                
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(CurrencyFormatter.formatRupiah(p.getTotal()), 545, y, paint);
                
                y += 20;
                paint.setTextSize(12);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                paint.setTextAlign(Paint.Align.LEFT);
                
                List<PesananItem> itemsList = p.getNamaMenu();
                StringBuilder menuBuilder = new StringBuilder();
                if (itemsList != null) {
                    for (int i = 0; i < itemsList.size(); i++) {
                        PesananItem item = itemsList.get(i);
                        menuBuilder.append(item.getNamaMenu())
                                   .append(" (")
                                   .append(item.getJumlah())
                                   .append("x)")
                                   .append(i == itemsList.size() - 1 ? "" : ", ");
                    }
                }
                canvas.drawText(menuBuilder.toString(), 50, y, paint);
                
                y += 30;
            }
            pdfDocument.finishPage(page);
        }

        // Page: Rincian Belanja Bahan
        if (belanjaList != null && !belanjaList.isEmpty()) {
            pageNum++;
            pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNum).create();
            page = pdfDocument.startPage(pageInfo);
            canvas = page.getCanvas();
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);
            
            paint.setColor(Color.BLACK);
            paint.setTextSize(20);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Rincian Pengeluaran Bahan Baku", 50, 60, paint);
            
            paint.setStrokeWidth(2);
            canvas.drawLine(50, 75, 545, 75, paint);
            
            y = 100;
            for (BelanjaBahan b : belanjaList) {
                if (y > 780) {
                    pdfDocument.finishPage(page);
                    pageNum++;
                    pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNum).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    paint.setColor(Color.WHITE);
                    canvas.drawPaint(paint);
                    y = 60;
                }
                
                String date = dateFmt.format(new Date(b.getTanggal()));
                paint.setColor(Color.BLACK);
                paint.setTextSize(14);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(date + " - " + b.getNamaBahan() + " [" + b.getMetodePembayaran() + "]", 50, y, paint);
                
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(CurrencyFormatter.formatRupiah(b.getTotalHarga()), 545, y, paint);
                
                y += 20;
                paint.setTextSize(12);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                paint.setTextAlign(Paint.Align.LEFT);
                String qtyStr = (b.getJumlahUnit() == (long) b.getJumlahUnit()) ? 
                    String.format("%d", (long) b.getJumlahUnit()) : 
                    String.valueOf(b.getJumlahUnit());
                canvas.drawText(b.getToko() + " - " + qtyStr + " " + b.getVolume() + " x " + CurrencyFormatter.formatRupiah(b.getHargaBeli()), 50, y, paint);
                
                y += 30;
            }
            pdfDocument.finishPage(page);
        }

        // Page: Rincian Biaya Operasional
        if (biayaList != null && !biayaList.isEmpty()) {
            pageNum++;
            pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNum).create();
            page = pdfDocument.startPage(pageInfo);
            canvas = page.getCanvas();
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);
            
            paint.setColor(Color.BLACK);
            paint.setTextSize(20);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("Rincian Biaya Operasional", 50, 60, paint);
            
            paint.setStrokeWidth(2);
            canvas.drawLine(50, 75, 545, 75, paint);
            
            y = 100;
            for (BiayaLain b : biayaList) {
                if (y > 780) {
                    pdfDocument.finishPage(page);
                    pageNum++;
                    pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pageNum).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    paint.setColor(Color.WHITE);
                    canvas.drawPaint(paint);
                    y = 60;
                }
                
                String date = dateFmt.format(new Date(b.getTanggal()));
                paint.setColor(Color.BLACK);
                paint.setTextSize(14);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(date + " - " + b.getKategori() + " [" + b.getMetodePembayaran() + "]", 50, y, paint);
                
                paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(CurrencyFormatter.formatRupiah(b.getJumlah()), 545, y, paint);
                
                y += 20;
                paint.setTextSize(12);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                paint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(b.getKeterangan(), 50, y, paint);
                
                y += 30;
            }
            pdfDocument.finishPage(page);
        }
        
        try {
            FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
            pdfDocument.writeTo(fos);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            pdfDocument.close();
        }
    }
    
    private static void drawRow(Canvas canvas, Paint paint, String label, String value, int leftX, int rightX, int y, boolean isBold) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(16);
        if (isBold) {
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        } else {
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        }
        
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(label, leftX, y, paint);
        
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(value, rightX, y, paint);
    }
}
