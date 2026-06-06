import Enums.ReaderStatus;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        LibraryService libraryService = new LibraryService();
        Reader readerIvan = new Reader(
                "reader 1",
                "Ivanov Ivan Ivanovich",
                LocalDate.of(1995, 5, 15),
                ReaderStatus.ACTIVE
        );


        Reader readerVasya = new Reader(
                "reader 11",
                "Ivanov Vasiliy Kizarovich",
                LocalDate.of(2000, 6, 25),
                ReaderStatus.ACTIVE
        );


    }
}
