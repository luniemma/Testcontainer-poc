import { Container, CheckCircle, Workflow, FileText, Github } from 'lucide-react';

function App() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100">
      {/* Header with AT&T Logo */}
      <header className="bg-white shadow-sm border-b border-slate-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4">
              <img
                src="https://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/AT%26T_logo_2016.svg/200px-AT%26T_logo_2016.svg.png"
                alt="AT&T Logo"
                className="h-12"
              />
              <div className="border-l border-slate-300 pl-4">
                <h1 className="text-2xl font-bold text-slate-900">
                  Testcontainers Smoke Test Framework
                </h1>
                <p className="text-sm text-slate-600">Enterprise-Grade Testing Infrastructure</p>
              </div>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Hero Section */}
        <div className="bg-white rounded-xl shadow-lg p-8 mb-8 border border-slate-200">
          <div className="flex items-start space-x-4">
            <div className="flex-shrink-0">
              <Container className="w-12 h-12 text-blue-600" />
            </div>
            <div className="flex-1">
              <h2 className="text-3xl font-bold text-slate-900 mb-4">
                Production-Ready Smoke Test Framework
              </h2>
              <p className="text-lg text-slate-700 mb-6 leading-relaxed">
                A comprehensive, reusable smoke test framework for the AT&T organization that validates
                Testcontainers functionality and external service connectivity during smoke testing.
                Built with enterprise reliability and scalability in mind.
              </p>
              <div className="flex flex-wrap gap-3">
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                  <CheckCircle className="w-4 h-4 mr-1" />
                  Production Ready
                </span>
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                  <Workflow className="w-4 h-4 mr-1" />
                  CI/CD Integrated
                </span>
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-purple-100 text-purple-800">
                  <Container className="w-4 h-4 mr-1" />
                  Docker Powered
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Feature Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          {/* Testcontainers Validation */}
          <div className="bg-white rounded-lg shadow-md p-6 border border-slate-200 hover:shadow-lg transition-shadow">
            <div className="flex items-center mb-4">
              <div className="bg-blue-100 p-3 rounded-lg">
                <Container className="w-6 h-6 text-blue-600" />
              </div>
              <h3 className="text-xl font-semibold text-slate-900 ml-3">
                Testcontainers Health Validation
              </h3>
            </div>
            <ul className="space-y-2 text-slate-700">
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>Automated container startup and health checks</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>Redis, Kafka, and Cassandra support</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>Detailed diagnostics (host, port, image, status)</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>Real-time container monitoring</span>
              </li>
            </ul>
          </div>

          {/* External Service Testing */}
          <div className="bg-white rounded-lg shadow-md p-6 border border-slate-200 hover:shadow-lg transition-shadow">
            <div className="flex items-center mb-4">
              <div className="bg-green-100 p-3 rounded-lg">
                <Workflow className="w-6 h-6 text-green-600" />
              </div>
              <h3 className="text-xl font-semibold text-slate-900 ml-3">
                External Service Connectivity
              </h3>
            </div>
            <ul className="space-y-2 text-slate-700">
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>HTTP/HTTPS endpoint validation</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>TCP connection testing</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>DNS resolution verification</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>Configurable required vs optional services</span>
              </li>
            </ul>
          </div>

          {/* Comprehensive Reporting */}
          <div className="bg-white rounded-lg shadow-md p-6 border border-slate-200 hover:shadow-lg transition-shadow">
            <div className="flex items-center mb-4">
              <div className="bg-purple-100 p-3 rounded-lg">
                <FileText className="w-6 h-6 text-purple-600" />
              </div>
              <h3 className="text-xl font-semibold text-slate-900 ml-3">
                Comprehensive Reporting
              </h3>
            </div>
            <ul className="space-y-2 text-slate-700">
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>Console output with real-time feedback</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>JSON reports for machine processing</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>HTML reports for visual review</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>Markdown reports for documentation</span>
              </li>
            </ul>
          </div>

          {/* CI/CD Integration */}
          <div className="bg-white rounded-lg shadow-md p-6 border border-slate-200 hover:shadow-lg transition-shadow">
            <div className="flex items-center mb-4">
              <div className="bg-orange-100 p-3 rounded-lg">
                <Github className="w-6 h-6 text-orange-600" />
              </div>
              <h3 className="text-xl font-semibold text-slate-900 ml-3">
                CI/CD Ready
              </h3>
            </div>
            <ul className="space-y-2 text-slate-700">
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>GitHub Actions integration</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>Jenkins pipeline support</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>GitLab CI compatibility</span>
              </li>
              <li className="flex items-start">
                <CheckCircle className="w-5 h-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                <span>Automated test execution</span>
              </li>
            </ul>
          </div>
        </div>

        {/* Supported Containers Section */}
        <div className="bg-white rounded-lg shadow-md p-8 border border-slate-200 mb-8">
          <h3 className="text-2xl font-bold text-slate-900 mb-6">Supported Testcontainers</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="text-center p-6 bg-gradient-to-br from-red-50 to-red-100 rounded-lg border border-red-200">
              <div className="text-4xl mb-3">🗄️</div>
              <h4 className="font-semibold text-lg text-slate-900 mb-2">Redis</h4>
              <p className="text-sm text-slate-700">In-memory data structure store</p>
              <p className="text-xs text-slate-600 mt-2">Version: 7-alpine</p>
            </div>
            <div className="text-center p-6 bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg border border-blue-200">
              <div className="text-4xl mb-3">📨</div>
              <h4 className="font-semibold text-lg text-slate-900 mb-2">Apache Kafka</h4>
              <p className="text-sm text-slate-700">Distributed streaming platform</p>
              <p className="text-xs text-slate-600 mt-2">Version: 7.5.0</p>
            </div>
            <div className="text-center p-6 bg-gradient-to-br from-green-50 to-green-100 rounded-lg border border-green-200">
              <div className="text-4xl mb-3">💾</div>
              <h4 className="font-semibold text-lg text-slate-900 mb-2">Apache Cassandra</h4>
              <p className="text-sm text-slate-700">Distributed NoSQL database</p>
              <p className="text-xs text-slate-600 mt-2">Version: 4.1</p>
            </div>
          </div>
        </div>

        {/* Quick Start */}
        <div className="bg-gradient-to-r from-blue-600 to-blue-700 rounded-lg shadow-md p-8 text-white">
          <h3 className="text-2xl font-bold mb-4">Quick Start</h3>
          <div className="bg-blue-800 bg-opacity-50 rounded-lg p-4 mb-4 font-mono text-sm overflow-x-auto">
            <p className="mb-2"># Run smoke tests locally</p>
            <p className="text-blue-200">mvn test -Dtest=EnhancedSmokeTest</p>
          </div>
          <p className="text-blue-100 mb-4">
            The framework automatically starts all required containers, validates their health,
            tests external service connectivity, and generates comprehensive reports.
          </p>
          <a
            href="https://github.com/your-org/testcontainers-smoke-test"
            className="inline-flex items-center px-6 py-3 bg-white text-blue-600 rounded-lg font-semibold hover:bg-blue-50 transition-colors"
          >
            <Github className="w-5 h-5 mr-2" />
            View on GitHub
          </a>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 mt-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <p className="text-slate-600 text-sm">
              © 2024 AT&T. Enterprise Testing Infrastructure.
            </p>
            <p className="text-slate-500 text-xs">
              Built with Testcontainers, Spring Boot, and React
            </p>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default App;
