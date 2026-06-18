import entity.Book;
import entity.BookCopy;
import entity.Reader;
import enums.BookType;
import enums.CopyStatus;
import enums.ReaderStatus;
import services.LibraryRules;
import services.LibraryService;
import services.OperationJournal;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        LibraryService libraryService = new LibraryService(
                new OperationJournal(),
                new LibraryRules()
        );
        Reader readerIvan = new Reader(
                "reader 1",
                "Ivanov Ivan Ivanovich",
                LocalDate.of(1995, 5, 15),
                ReaderStatus.ACTIVE
        );
        Reader readerVasya = new Reader(
                "reader 2",
                "Ivanov Vasiliy Kizarovich",
                LocalDate.of(2000, 6, 25),
                ReaderStatus.BLOCKED
        );
//        Reader readerPetya = new Reader(
//                "reader 3",
//                "Ivanov Vasiliy Kizarovich",
//                LocalDate.of(2000, 6, 25),
//                ReaderStatus.ACTIVE
//        );
        Book book = new Book("123124124535", "Avatar", "James Kameron", 2001, BookType.PAPER);
        BookCopy bookCopyAvatar1 = new BookCopy("copy-1", book, CopyStatus.AVAILABLE);
        BookCopy bookCopyAvatar2 = new BookCopy("copy-2", book, CopyStatus.AVAILABLE);
        //BookCopy bookCopyAvatar3 = new BookCopy("copy-3", book, CopyStatus.AVAILABLE);
        libraryService.addReader(readerIvan);
        libraryService.addReader(readerVasya);
        // libraryService.addReader(readerPetya);
        libraryService.addCopy(bookCopyAvatar1);
        libraryService.addCopy(bookCopyAvatar2);
        //libraryService.addCopy(bookCopyAvatar3);
        System.out.println(libraryService.borrowBook(readerIvan, bookCopyAvatar1, 10));
//        libraryService.borrowBook(readerIvan, bookCopyAvatar1, 12);
        System.out.println(libraryService.borrowBook(readerIvan, bookCopyAvatar1, 5));
        System.out.println(libraryService.borrowBook(readerVasya, bookCopyAvatar2, 5));
        System.out.println(libraryService.returnBook(readerIvan, bookCopyAvatar1));
        System.out.println(libraryService.borrowBook(readerIvan, bookCopyAvatar1, 10));
//        System.out.println(libraryService.returnBook(readerIvan, bookCopyAvatar1));
//        System.out.println(libraryService.returnBook(readerIvan, bookCopyAvatar2));
//        System.out.println(libraryService.returnBook(readerVasya, bookCopyAvatar2));
//        System.out.println(libraryService.returnBook(readerVasya, bookCopyAvatar2));
//        libraryService.borrowBook(readerIvan, bookCopyAvatar1, 2);
        libraryService.markLost(readerIvan, bookCopyAvatar1, "Сжег из-за концовки");
        System.out.println(libraryService.returnBook(readerIvan, bookCopyAvatar1));
        System.out.println(libraryService.findAvailableCopiesByIsbn("123124124535"));
        System.out.println(libraryService.getAllReaders());
        System.out.println(libraryService.getAllCopies());
        System.out.println(libraryService.getAllLoans());
        System.out.println(libraryService.getJournal());
        System.out.println(libraryService.borrowBook(readerIvan, bookCopyAvatar2, 10));
        System.out.println(libraryService.findActiveLoansByReader(readerIvan));
        System.out.println(libraryService.findOverdueLoans(LocalDate.ofEpochDay(20)));

    }
}
