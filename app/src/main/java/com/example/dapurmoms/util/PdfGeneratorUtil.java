package com.example.dapurmoms.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.ParcelFileDescriptor;

import com.example.dapurmoms.data.database.entity.Pesanan;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PdfGeneratorUtil {

    public static boolean generateReceiptPdf(Context context, ParcelFileDescriptor pfd, Pesanan pesanan) {
        PdfDocument pdfDocument = new PdfDocument();
        
        // Custom receipt size (400x600)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        
        // Background
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        
        // Header
        paint.setColor(Color.BLACK);
        paint.setTextSize(26);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("DAPUR MOMS HIJRA", 200, 50, paint);
        
        paint.setTextSize(14);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Aneka Masakan & Kue", 200, 75, paint);
        canvas.drawText("Telp: 0822 8889 7288", 200, 95, paint);
        
        // Divider
        paint.setStrokeWidth(2);
        canvas.drawLine(20, 115, 380, 115, paint);
        
        // Content
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(14);
        
        int y = 145;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
        String dateStr = sdf.format(new Date(pesanan.getTanggal()));
        
        canvas.drawText("Tanggal   : " + dateStr, 20, y, paint);
        y += 25;
        
        String namaPemesan = pesanan.getNamaPemesan().equals("-") ? "Pelanggan" : pesanan.getNamaPemesan();
        canvas.drawText("Pemesan : " + namaPemesan, 20, y, paint);
        y += 40;
        
        // Items Header
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Pesanan", 20, y, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Total", 380, y, paint);
        
        y += 15;
        paint.setStrokeWidth(1);
        canvas.drawLine(20, y, 380, y, paint);
        y += 30;
        
        // Item
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText(pesanan.getNamaMenu(), 20, y, paint);
        
        y += 20;
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(13);
        canvas.drawText(pesanan.getJumlah() + " x " + CurrencyFormatter.formatRupiah(pesanan.getHargaSatuan()), 20, y, paint);
        
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(Color.BLACK);
        paint.setTextSize(14);
        canvas.drawText(CurrencyFormatter.formatRupiah(pesanan.getTotal()), 380, y, paint);
        
        y += 30;
        paint.setStrokeWidth(2);
        canvas.drawLine(20, y, 380, y, paint);
        y += 35;
        
        // Total Footer
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("TOTAL PEMBAYARAN", 20, y, paint);
        
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(18);
        canvas.drawText(CurrencyFormatter.formatRupiah(pesanan.getTotal()), 380, y, paint);
        
        y += 70;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint.setTextSize(14);
        canvas.drawText("Terima Kasih atas Pesanan Anda!", 200, y, paint);
        
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

    public static boolean generateLaporanPdf(Context context, ParcelFileDescriptor pfd, String monthStr, long pendapatan, long biayaBahan, long biayaOps, long hpp, long untung, double margin) {
        PdfDocument pdfDocument = new PdfDocument();
        // A4 Size: 595 x 842 points
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        
        // Background
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        
        // Header
        paint.setColor(Color.BLACK);
        paint.setTextSize(28);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("DAPUR MOMS HIJRA", 297, 80, paint);
        
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Laporan Keuangan Bulanan", 297, 110, paint);
        canvas.drawText("Periode: " + monthStr, 297, 135, paint);
        
        // Divider
        paint.setStrokeWidth(2);
        canvas.drawLine(50, 160, 545, 160, paint);
        
        int y = 220;
        int leftX = 80;
        int rightX = 515;
        
        // Draw Row helper
        drawRow(canvas, paint, "Total Pendapatan (Penjualan)", CurrencyFormatter.formatRupiah(pendapatan), leftX, rightX, y, true);
        y += 40;
        
        paint.setStrokeWidth(1);
        canvas.drawLine(leftX, y, rightX, y, paint);
        y += 40;
        
        drawRow(canvas, paint, "Pengeluaran Bahan Baku", "- " + CurrencyFormatter.formatRupiah(biayaBahan), leftX, rightX, y, false);
        y += 35;
        
        drawRow(canvas, paint, "Pengeluaran Operasional", "- " + CurrencyFormatter.formatRupiah(biayaOps), leftX, rightX, y, false);
        y += 30;
        
        canvas.drawLine(leftX, y, rightX, y, paint);
        y += 40;
        
        drawRow(canvas, paint, "Total Harga Pokok Penjualan (HPP)", CurrencyFormatter.formatRupiah(hpp), leftX, rightX, y, true);
        y += 40;
        
        canvas.drawLine(leftX, y, rightX, y, paint);
        y += 50;
        
        // Laba Bersih
        paint.setTextSize(20);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.LEFT);
        String labelUntung = (untung >= 0) ? "Laba Bersih" : "Rugi Bersih";
        canvas.drawText(labelUntung, leftX, y, paint);
        
        paint.setTextAlign(Paint.Align.RIGHT);
        if (untung < 0) paint.setColor(Color.RED);
        else paint.setColor(Color.parseColor("#388E3C")); // Green
        canvas.drawText(CurrencyFormatter.formatRupiah(untung), rightX, y, paint);
        
        y += 40;
        paint.setColor(Color.BLACK);
        paint.setTextSize(16);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Margin Keuntungan", leftX, y, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format(new Locale("id", "ID"), "%.1f%%", margin), rightX, y, paint);
        
        y += 100;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        paint.setTextSize(12);
        canvas.drawText("Laporan dibuat secara otomatis oleh Aplikasi Catatan Keuangan Dapur Moms", 297, 800, paint);
        
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
