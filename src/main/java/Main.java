import config.HibernateConfig;
import config.HibernateTransactionExecutor;
import entity.Book;
import entity.BookCopy;
import entity.Reader;
import enums.BookType;
import enums.CopyStatus;
import enums.ReaderStatus;
import org.hibernate.SessionFactory;
import repository.BookCopyRepository;
import repository.BookRepository;
import repository.LoanRepository;
import repository.ReaderRepository;
import services.LibraryRules;
import services.LibraryService;
import services.OperationJournal;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateConfig.getSessionFactory();
        BookRepository bookRepository = new BookRepository(sessionFactory);
        ReaderRepository readerRepository = new ReaderRepository(sessionFactory);
        BookCopyRepository bookCopyRepository = new BookCopyRepository(sessionFactory);
        LoanRepository loanRepository = new LoanRepository(sessionFactory);
        HibernateTransactionExecutor hibernateTransactionExecutor = new HibernateTransactionExecutor(sessionFactory);
        OperationJournal operationJournal = new OperationJournal();
        LibraryRules libraryRules = new LibraryRules();
        LibraryService libraryService = new LibraryService(
                bookRepository,
                readerRepository,
                bookCopyRepository,
                loanRepository,
                hibernateTransactionExecutor,
                operationJournal,
                libraryRules
        );
        Reader readerIvan = new Reader(
                "reader 1",
                "Ivanov Ivan Ivanovich",
                LocalDate.of(1995, 5, 15),
                ReaderStatus.ACTIVE
        );

        Book book = new Book("123124124535", "Avatar", "James Kameron", 2001, BookType.PAPER);
        BookCopy bookCopyAvatar1 = new BookCopy("copy-1", book, CopyStatus.AVAILABLE);

        libraryService.addBook(book);
        libraryService.addReader(readerIvan);
        libraryService.addCopy(bookCopyAvatar1);

        System.out.println(libraryService.borrowBook(readerIvan, bookCopyAvatar1, 10));

        //libraryService.markLost(readerIvan, bookCopyAvatar1, "Сжег из-за концовки");

        System.out.println(libraryService.findActiveLoansByReader(readerIvan));

        HibernateConfig.closeSessionFactory();

    }
}
