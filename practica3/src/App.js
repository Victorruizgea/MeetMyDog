import React, { useState } from 'react';
import axios from 'axios';
import './styles.css';

function App() {
  const [searchTerm, setSearchTerm] = useState('');
  const [bookList, setBookList] = useState([]);
  const [error, setError] = useState('');

  const handleSearch = async (event) => {
    event.preventDefault();

    try {
      const response = await axios.get('https://www.googleapis.com/books/v1/volumes', { params: { q: searchTerm }});
      const data = response.data;

      if (data && data.items) {
        setBookList(data.items);
        setError('');
      } else {
        setBookList([]);
        setError('No se encontraron resultados.');
      }
    } catch (error) {
      console.error(error);
      setBookList([]);
      setError('Ocurrió un error al realizar la búsqueda.');
    }
  };

  return (
    <div>
      <h1>Buscador de Libros</h1>

      <form onSubmit={handleSearch}>
        <input type="text" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} placeholder="Introduce el nombre del libro" />
        <button type="submit">Buscar</button>
      </form>

      {error && <p>{error}</p>}

      <div>
        {bookList.map((book) => (
          <div key={book.id}>
            <img src={book.volumeInfo.imageLinks?.thumbnail} alt="Portada del libro" />
            <h3>{book.volumeInfo.title}</h3>
            <p>Autores: {book.volumeInfo.authors ? book.volumeInfo.authors.join(', ') : 'Autor desconocido'}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;
