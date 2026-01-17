package com.stockmarket.logic;

import com.stockmarket.domain.Commodity;
import com.stockmarket.domain.Currency;
import com.stockmarket.domain.PurchaseLot;
import com.stockmarket.domain.Share;
import com.stockmarket.exceptions.DataIntegrityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioPersistenceTest {

    @TempDir
    Path tempDir;

    @Test
    void saveAndLoadShouldRoundTripCashAssetsAndLots() throws Exception {
        Portfolio portfolio = new Portfolio(10500.50);
        Share aapl = new Share("AAPL", "Apple", 150.00);

        Position pos = new Position(aapl);
        pos.addLot(new PurchaseLot(LocalDate.parse("2023-05-10"), 150.00, 10.0));
        pos.addLot(new PurchaseLot(LocalDate.parse("2023-06-12"), 155.00, 5.0));
        portfolio.getPositions().put("AAPL", pos);

        Path file = tempDir.resolve("portfolio.txt");
        PortfolioPersistence persistence = new PortfolioPersistence();
        persistence.save(portfolio, file);

        assertTrue(Files.exists(file));

        Portfolio loaded = persistence.load(file);
        assertEquals(10500.50, loaded.getCash(), 0.0001);
        assertNotNull(loaded.getPosition("AAPL"));
        assertEquals(1, loaded.getHoldingsCount());

        Position loadedPos = loaded.getPosition("AAPL");
        assertEquals(15.0, loadedPos.getTotalQuantity(), 0.0001);
        assertEquals(2, loadedPos.getLots().size());
    }

    @Test
    void loadShouldRejectLotWithoutAsset() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file,
                "HEADER|CASH|1000.0\n" +
                "LOT|2023-05-10|10|150.0\n"
        );

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldRejectInvalidNumberFormatting() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file,
                "HEADER|CASH|not_a_number\n"
        );

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldRejectMismatchedDeclaredQuantity() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file,
                "HEADER|CASH|1000.0\n" +
                "ASSET|SHARE|AAPL|15|Apple|150.0\n" +
                "LOT|2023-05-10|10|150.0\n"
        );

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldRejectBadLotLineFormat() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file,
                "HEADER|CASH|1000.0\n" +
                "ASSET|SHARE|AAPL|0|Apple|150.0\n" +
                "LOT|2023-05-10|10\n"
        );

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldRejectEmptyFile() throws Exception {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldRejectInvalidHeaderTokens() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file, "HEAD|CASH|1000.0\n");

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldRejectInvalidHeaderFormatLength() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file, "HEADER|CASH\n");

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldRejectUnknownLineType() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file,
                "HEADER|CASH|1000.0\n" +
                "WAT|X\n"
        );

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldRejectInvalidAssetType() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file,
                "HEADER|CASH|1000.0\n" +
                "ASSET|NOPE|AAA|0|Name|10.0\n"
        );

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldRejectInvalidAssetLineLength() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file,
                "HEADER|CASH|1000.0\n" +
                "ASSET|SHARE|AAA|Name\n"
        );

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldRejectInvalidLotDate() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file,
                "HEADER|CASH|1000.0\n" +
                "ASSET|SHARE|AAA|1|Name|10.0\n" +
                "LOT|not-a-date|1|10.0\n"
        );

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldRejectNonPositiveLotQuantity() throws Exception {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file,
                "HEADER|CASH|1000.0\n" +
                "ASSET|SHARE|AAA|0|Name|10.0\n" +
                "LOT|2023-01-01|0|10.0\n"
        );

        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(DataIntegrityException.class, () -> persistence.load(file));
    }

    @Test
    void loadShouldSupportLegacyAssetLineFormat() throws Exception {
        Path file = tempDir.resolve("legacy.txt");
        Files.writeString(file,
                "HEADER|CASH|1000.0\n" +
                "ASSET|SHARE|AAA|Name|10.0\n" +
                "LOT|2023-01-01|2|10.0\n"
        );

        PortfolioPersistence persistence = new PortfolioPersistence();
        Portfolio loaded = persistence.load(file);

        assertEquals(1000.0, loaded.getCash(), 0.0001);
        assertNotNull(loaded.getPosition("AAA"));
        assertEquals(2.0, loaded.getPosition("AAA").getTotalQuantity(), 0.0001);
    }

    @Test
    void loadShouldCreateCorrectAssetSubclassPerType() throws Exception {
        Path file = tempDir.resolve("types.txt");
        Files.writeString(file,
                "HEADER|CASH|1000.0\n" +
                "ASSET|COMMODITY|GOLD|1|Gold|10.0\n" +
                "LOT|2023-01-01|1|10.0\n" +
                "ASSET|CURRENCY|EUR|1|Euro|10.0\n" +
                "LOT|2023-01-01|1|10.0\n"
        );

        PortfolioPersistence persistence = new PortfolioPersistence();
        Portfolio loaded = persistence.load(file);

        assertTrue(loaded.getPosition("GOLD").getAsset() instanceof Commodity);
        assertTrue(loaded.getPosition("EUR").getAsset() instanceof Currency);
    }

    @Test
    void saveShouldValidateArgsAndWrapIoErrors() {
        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(IllegalArgumentException.class, () -> persistence.save(null, tempDir.resolve("x")));
        assertThrows(IllegalArgumentException.class, () -> persistence.save(new Portfolio(0.0), null));

        // zapis do katalogu powinien wywołać błąd I/O
        assertThrows(DataIntegrityException.class, () -> persistence.save(new Portfolio(0.0), tempDir));
    }

    @Test
    void loadShouldValidateArgsAndWrapIoErrors() {
        PortfolioPersistence persistence = new PortfolioPersistence();
        assertThrows(IllegalArgumentException.class, () -> persistence.load(null));

        // brak pliku -> błąd I/O
        Path missing = tempDir.resolve("missing.txt");
        assertThrows(DataIntegrityException.class, () -> persistence.load(missing));
    }
}

