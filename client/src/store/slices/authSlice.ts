import { getCurrentUser, loginUser, registerUser } from '@/lib/api'
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit'

interface AuthState {
  user: User | null
  isLoading: boolean
  error: string | null
  isAuthenticated: boolean
}

const initialState: AuthState = {
  user: null,
  isLoading: false,
  error: null,
  isAuthenticated: false
}

export interface User {
  id: string
  email: string
  username: string
}

export const fetchCurrentUser = createAsyncThunk<User>(
  'auth/fetchCurrentUser',
  async (_, { rejectWithValue }) => {
    try {
      return await getCurrentUser()
    } catch (error) {
      const message =
        error instanceof Error ? error.message : 'Failed to fetch user'
      return rejectWithValue(message)
    }
  }
)

export const authLogin = createAsyncThunk<
  User,
  { email: string; password: string },
  { rejectValue: string | null }
>('auth/login', async ({ email, password }, { rejectWithValue }) => {
  try {
    return await loginUser(email, password)
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Login failed'
    return rejectWithValue(message)
  }
})

export const authRegister = createAsyncThunk<
  User,
  {
    email: string
    username: string
    password: string
    role: 'ROLE_USER' | 'ROLE_ADMIN'
  },
  { rejectValue: string | null }
>(
  'auth/register',
  async ({ email, username, password, role }, { rejectWithValue }) => {
    try {
      return await registerUser(email, password, username, role)
    } catch (error) {
      const message =
        error instanceof Error ? error.message : 'Registration failed'
      return rejectWithValue(message)
    }
  }
)

const authReducer = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearError: state => {
      state.error = null
    },
    setUser: (state, action) => {
      state.user = action.payload
    }
  },
  extraReducers: builder => {
    builder
      .addCase(fetchCurrentUser.pending, state => {
        state.isLoading = true
        state.error = null
        state.isAuthenticated = !!state.user
      })
      .addCase(fetchCurrentUser.fulfilled, (state, action) => {
        state.isLoading = false
        state.user = action.payload
        state.isAuthenticated = !!state.user
        state.error = null
      })
      .addCase(fetchCurrentUser.rejected, state => {
        state.isLoading = false
        state.user = null
        state.isAuthenticated = !!state.user
        state.error = null
      })

    builder
      .addCase(authLogin.pending, state => {
        state.isLoading = true
        state.error = null
        state.isAuthenticated = !!state.user
      })
      .addCase(authLogin.fulfilled, (state, action) => {
        state.isLoading = false
        state.user = action.payload
        state.isAuthenticated = !!state.user
      })
      .addCase(authLogin.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload ?? null
        state.isAuthenticated = !!state.user
      })

    builder
      .addCase(authRegister.pending, state => {
        state.isLoading = true
        state.error = null
        state.isAuthenticated = !!state.user
      })
      .addCase(authRegister.fulfilled, (state, action) => {
        state.isLoading = false
        state.user = action.payload
        state.isAuthenticated = !!state.user
      })
      .addCase(authRegister.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload ?? null
        state.isAuthenticated = !!state.user
      })
  }
})

export const { clearError, setUser } = authReducer.actions
export default authReducer.reducer
